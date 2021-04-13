package com.ocram.qichwadic.features.search.ui.results.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.ui.activity.MainActivity
import com.ocram.qichwadic.features.search.ui.SearchViewModel
import com.ocram.qichwadic.features.search.ui.SearchViewState
import com.ocram.qichwadic.features.search.ui.custom.EndlessRecyclerViewScrollListener
import com.ocram.qichwadic.features.search.ui.results.SearchResultsRenderer
import kotlinx.android.synthetic.main.fragment_search_results.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SearchResultsFragment : Fragment(), SearchResultListAdapter.DefinitionClickListener, SearchResultsRenderer {

    private var offlineSearch: Boolean = false
    private lateinit var spinnerResultAdapter: SpinnerResultAdapter
    private lateinit var searchResultListAdapter: SearchResultListAdapter
    private var scrollListener: EndlessRecyclerViewScrollListener? = null

    private val searchViewModel: SearchViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerViews()
        setAdapters()

        spResults.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                onItemSelected(pos)
            }
        }
        btnNoResultsGetDictionaries.setOnClickListener {
            findNavController().navigate(R.id.search_fragment_to_dictionaries_fragment)
        }
        searchViewModel.resultLiveData.observe(this, Observer<List<SearchResultModel>> { this.onResultsChanged(it) })
        searchViewModel.searchViewState.observe(this, Observer<SearchViewState> { this.onSearchViewStateChanged(it) })
        searchViewModel.extraDefinitions.observe(this, Observer<List<DefinitionModel>> { this.onFetchExtraDefinitions(it) })
        searchViewModel.loadFetchMore.observe(this, Observer<Boolean> { this.onFetchMoreLoadingChanged(it) })
        searchViewModel.offlineSearch.observe(this, Observer<Boolean> { this.onSearchModeChanged(it) })
    }

    private fun setRecyclerViews() {
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rvResults.layoutManager = layoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                val currentSearchResult = spResults.selectedItem as SearchResultModel
                val totalToFetch = currentSearchResult.total
                if (totalToFetch > 20 && searchResultListAdapter.itemCount < totalToFetch) {
                    val dictionaryId = currentSearchResult.dictionaryId
                    searchViewModel.fetchMoreResults(dictionaryId, page + 1)
                }
            }
        }
        rvResults.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
    }

    private fun setAdapters() {
        spinnerResultAdapter = SpinnerResultAdapter(requireContext(), R.layout.item_spinner_result, ArrayList())
        searchResultListAdapter = SearchResultListAdapter(ArrayList(), this)
        rvResults.adapter = searchResultListAdapter
        spResults.adapter = spinnerResultAdapter
    }

    private fun onItemSelected(pos: Int) {
        Log.d(javaClass.name, "item selected $pos")
        val result = spResults.getItemAtPosition(pos) as SearchResultModel
        selectResult(result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollListener?.onDestroy()
        scrollListener = null
        rvResults.adapter = null
        spResults.adapter = null
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
        llResultsArea.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        if (searchResults == null || searchResults.isEmpty()) {
            spinnerResultAdapter.setSearchResults(emptyList())
            searchResultListAdapter.setDefinitions(mutableListOf())
            spResults.visibility = View.GONE
            rvResults.visibility = View.GONE
            if (offlineSearch) {
                llNoResultsForOffline.visibility = View.VISIBLE
            } else {
                llNoResultsForOnline.visibility = View.VISIBLE
            }
        } else {
            llNoResultsForOffline.visibility = View.GONE
            llNoResultsForOnline.visibility = View.GONE
            spResults.visibility = View.VISIBLE
            renderResults(searchResults)
        }
    }

    private fun renderResults(searchResults: List<SearchResultModel>) {
        val previousSelection = spResults.selectedItemPosition

        spinnerResultAdapter.setSearchResults(searchResults)
        spinnerResultAdapter.notifyDataSetChanged()
        spResults.setSelection(0, true)
        if (previousSelection == 0) {
            spinnerResultAdapter.getItem(0)?.let { selectedItem -> selectResult(selectedItem) }
        }
    }

    private fun selectResult(result: SearchResultModel) {
        rvResults.scrollToPosition(0)
        scrollListener?.resetState()
        rvResults.visibility = View.VISIBLE
        searchResultListAdapter.setDefinitions(result.definitions)
        searchResultListAdapter.notifyDataSetChanged()
    }

    override fun onSearchViewStateChanged(searchViewState: SearchViewState) {
        when(searchViewState) {
            SearchViewState.LOADING -> {
                searchProgressBar.visibility = View.VISIBLE
                llResultsArea.visibility = View.GONE
                llNoResultsForOffline.visibility = View.GONE
                llNoResultsForOnline.visibility = View.GONE
                tvError.visibility = View.GONE
            }
            SearchViewState.SUCCESS -> {
                searchProgressBar.visibility = View.GONE
                tvError.visibility = View.GONE
            }
            SearchViewState.ERROR -> {
                searchProgressBar.visibility = View.GONE
                val errorMsgId = if (offlineSearch) R.string.error_offline else R.string.error_online
                tvError.text = getString(errorMsgId)

                llResultsArea.visibility = View.GONE
                llNoResultsForOffline.visibility = View.GONE
                llNoResultsForOnline.visibility = View.GONE
                tvError.visibility = View.VISIBLE
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
        fetchMoreProgressBar.visibility = if (isLoading == null || !isLoading) View.GONE else View.VISIBLE
    }
}
