package com.ocram.qichwadic.features.search.ui.results.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.ui.activity.MainActivity
import com.ocram.qichwadic.databinding.FragmentSearchResultsBinding
import com.ocram.qichwadic.features.search.ui.SearchViewModel
import com.ocram.qichwadic.features.search.ui.SearchViewState
import com.ocram.qichwadic.features.search.ui.custom.EndlessRecyclerViewScrollListener
import com.ocram.qichwadic.features.search.ui.results.SearchResultsRenderer
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SearchResultsFragment : Fragment(), SearchResultListAdapter.DefinitionClickListener, SearchResultsRenderer {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    private var offlineSearch: Boolean = false
    private lateinit var spinnerResultAdapter: SpinnerResultAdapter
    private lateinit var searchResultListAdapter: SearchResultListAdapter
    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    private val searchViewModel: SearchViewModel by activityViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerViews()
        setAdapters()

        binding.spResults.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                onItemSelected(pos)
            }
        }
        binding.btnNoResultsGetDictionaries.setOnClickListener {
            findNavController().navigate(R.id.search_fragment_to_dictionaries_fragment)
        }
        searchViewModel.resultLiveData.observe(viewLifecycleOwner) { this.onResultsChanged(it) }
        searchViewModel.searchViewState.observe(viewLifecycleOwner) { this.onSearchViewStateChanged(it) }
        searchViewModel.extraDefinitions.observe(viewLifecycleOwner) { this.onFetchExtraDefinitions(it) }
        searchViewModel.loadFetchMore.observe(viewLifecycleOwner) { this.onFetchMoreLoadingChanged(it) }
        searchViewModel.offlineSearch.observe(viewLifecycleOwner) { this.onSearchModeChanged(it) }
    }

    private fun setRecyclerViews() {
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvResults.layoutManager = layoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                val currentSearchResult = binding.spResults.selectedItem as SearchResultModel
                val totalToFetch = currentSearchResult.total
                if (totalToFetch > 20 && searchResultListAdapter.itemCount < totalToFetch) {
                    val dictionaryId = currentSearchResult.dictionaryId
                    searchViewModel.fetchMoreResults(dictionaryId, page + 1)
                }
            }
        }
        binding.rvResults.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    private fun setAdapters() {
        spinnerResultAdapter = SpinnerResultAdapter(requireContext(), R.layout.item_spinner_result, ArrayList())
        searchResultListAdapter = SearchResultListAdapter(mutableListOf(), this)
        binding.rvResults.adapter = searchResultListAdapter
        binding.spResults.adapter = spinnerResultAdapter
    }

    private fun onItemSelected(pos: Int) {
        Log.d(javaClass.name, "item selected $pos")
        val result = binding.spResults.getItemAtPosition(pos) as SearchResultModel
        selectResult(result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollListener?.onDestroy()
        scrollListener = null
        binding.rvResults.adapter = null
        binding.spResults.adapter = null
    }

    override fun onItemShareClick(definition: DefinitionModel) {
        val text = getString(R.string.share_definition_from_dictionary,
                definition.dictionaryName, definition.word, definition.meaning)
        (requireActivity() as MainActivity)
                .openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
    }

    override fun onItemFavoriteClick(definition: DefinitionModel) {
        searchViewModel.saveFavorite(definition)
    }

    private fun onSearchModeChanged(isOffline: Boolean?) {
        this.offlineSearch = isOffline != null && isOffline
    }

    override fun onResultsChanged(searchResults: List<SearchResultModel>?) {
        binding.llResultsArea.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        if (searchResults == null || searchResults.isEmpty()) {
            spinnerResultAdapter.setSearchResults(emptyList())
            searchResultListAdapter.setDefinitions(mutableListOf())
            binding.spResults.visibility = View.GONE
            binding.rvResults.visibility = View.GONE
            if (offlineSearch) {
                binding.llNoResultsForOffline.visibility = View.VISIBLE
            } else {
                binding.llNoResultsForOnline.visibility = View.VISIBLE
            }
        } else {
            binding.llNoResultsForOffline.visibility = View.GONE
            binding.llNoResultsForOnline.visibility = View.GONE
            binding.spResults.visibility = View.VISIBLE
            renderResults(searchResults)
        }
    }

    private fun renderResults(searchResults: List<SearchResultModel>) {
        val previousSelection = binding.spResults.selectedItemPosition

        spinnerResultAdapter.setSearchResults(searchResults)
        spinnerResultAdapter.notifyDataSetChanged()
        binding.spResults.setSelection(0, true)
        if (previousSelection == 0) {
            spinnerResultAdapter.getItem(0)?.let { selectedItem -> selectResult(selectedItem) }
        }
    }

    private fun selectResult(result: SearchResultModel) {
        binding.rvResults.scrollToPosition(0)
        scrollListener?.resetState()
        binding.rvResults.visibility = View.VISIBLE
        searchResultListAdapter.setDefinitions(result.definitions)
    }

    override fun onSearchViewStateChanged(searchViewState: SearchViewState) {
        when(searchViewState) {
            SearchViewState.LOADING -> {
                binding.searchProgressBar.visibility = View.VISIBLE
                binding.llResultsArea.visibility = View.GONE
                binding.llNoResultsForOffline.visibility = View.GONE
                binding.llNoResultsForOnline.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
            SearchViewState.SUCCESS -> {
                binding.searchProgressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
            SearchViewState.ERROR -> {
                binding.searchProgressBar.visibility = View.GONE
                val errorMsgId = if (offlineSearch) R.string.error_offline else R.string.error_online
                binding.tvError.text = getString(errorMsgId)

                binding.llResultsArea.visibility = View.GONE
                binding.llNoResultsForOffline.visibility = View.GONE
                binding.llNoResultsForOnline.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun onFetchExtraDefinitions(definitions: List<DefinitionModel>?) {
        if (!definitions.isNullOrEmpty()) {
            val currentLength = searchResultListAdapter.itemCount
            searchResultListAdapter.pushMoreDefinitions(definitions)
            searchResultListAdapter.notifyItemRangeInserted(currentLength, definitions.size)
        }
    }

    private fun onFetchMoreLoadingChanged(isLoading: Boolean?) {
        binding.fetchMoreProgressBar.visibility = if (isLoading == null || !isLoading) View.GONE else View.VISIBLE
    }
}
