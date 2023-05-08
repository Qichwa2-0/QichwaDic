package com.ocram.qichwadic.features.search.ui

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.DictLang
import com.ocram.qichwadic.core.ui.Event
import com.ocram.qichwadic.core.ui.SearchParams
import com.ocram.qichwadic.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.max

class SearchFragment : Fragment() {

    private lateinit var placeholders: Map<String, String>
    private lateinit var quechuaPlaceholder: String
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        if (BuildConfig.DEBUGGABLE) {
            this.activateStrictMode()
        }
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        loadPlaceholders()
        return binding.root
    }

    private fun activateStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }

    private fun loadPlaceholders() {
        this.quechuaPlaceholder = getString(R.string.search_quechua_placeholder)
        this.placeholders = mapOf(
                Pair("qu", quechuaPlaceholder),
                Pair("es", getString(R.string.search_spanish_placeholder)),
                Pair("en", getString(R.string.search_english_placeholder)),
                Pair("fr", getString(R.string.search_french_placeholder)),
                Pair("de", getString(R.string.search_german_placeholder)),
                Pair("it", getString(R.string.search_italian_placeholder)),
                Pair("ru", getString(R.string.search_russian_placeholder))
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        val toolbar = view.findViewById<Toolbar>(R.id.mToolbar)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer)

        toolbar.setupWithNavController(navController, appBarConfiguration)

        setAdapters()
        initSearchView()

        binding.ivSwapLanguages.setOnClickListener { swapLanguages() }
        binding.fabSearch.setOnClickListener { runSearch() }

        binding.spDictLangs.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                binding.spDictLangs.selectedItem.let {
                    searchViewModel.saveNonQuechuaLangPos((it as DictLang).code)
                }
            }
        }

        binding.spSearchTypes.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                searchViewModel.saveSearchType(p2)
            }
        }

        binding.swOfflineSearch.setOnCheckedChangeListener { _, checked -> toggleOfflineSearch(checked) }

        view.findViewById<ImageView>(R.id.ivSubmitSearch).setOnClickListener { runSearch() }

        searchViewModel.searchParams.observe(viewLifecycleOwner) { this.setSearchParams(it) }
        searchViewModel.saveFavoriteResult.observe(viewLifecycleOwner
        ) { this.onSaveWordResult(it) }
        searchViewModel.offlineSearch.observe(viewLifecycleOwner) { this.onSearchModeChanged(it) }
        searchViewModel.searchFromQuechua.observe(viewLifecycleOwner) {
            this.updateViewsOnLangChange(it)
        }
    }

    private fun setAdapters() {
        val searchTypesAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.searchTypes, R.layout.item_spinner_search_type)
        searchTypesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_search_type)
        binding.spSearchTypes.adapter = searchTypesAdapter
        binding.spSearchTypes.setSelection(2)

        binding.spDictLangs.adapter = SearchDictLangAdapter(requireContext())
    }

    private fun initSearchView() {
        binding.mToolbar.svSearch.apply {
            setQuery(this.queryHint, false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(javaClass.name, "onquerysubmit $query")
                    searchViewModel.searchWord(query)
                    this@apply.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return true
                }
            })
        }
    }

    private fun runSearch() {
        binding.root.findViewById<SearchView>(R.id.svSearch).apply {
            if (query.isNotEmpty()) {
                setQuery(query, true)
            }
        }
    }

    private fun swapLanguages() {
        searchViewModel.saveSearchFromQuechua()
    }

    private fun toggleOfflineSearch(checked: Boolean) {
        searchViewModel.changeSearchModeConfig(checked)
    }

    private fun onSaveWordResult(isSuccess: Event<Boolean>) {
        isSuccess.getContentIfNotHandled()?.let {
            val stringId = if (it) R.string.favorite_added_success else R.string.favorite_added_error
            Snackbar.make(binding.clSearch, getString(stringId), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.see_favorites)) {
                        findNavController().navigate(R.id.search_fragment_to_favorites_fragment)
                    }.show()
        }
    }

    private fun onSearchModeChanged(isOffline: Boolean?) {
        binding.swOfflineSearch.isChecked = isOffline ?: false
    }

    private fun setSearchParams(searchParams: Event<SearchParams>) {
        val spDictLangs = binding.spDictLangs
        searchParams.getContentIfNotHandled()?.let {
            if (!it.isFromQuechua) {
                this.updateViewsOnLangChange(it.isFromQuechua)
            }
            binding.spSearchTypes.setSelection(it.searchTypePos)
            val dictLangPos = (spDictLangs.adapter as SearchDictLangAdapter).getPosByValue(it.nonQuechuaLangCode)
            spDictLangs.setSelection(max(0, dictLangPos))

            updateQueryHint(it.isFromQuechua)
            val textToSearch =
                it.searchWord.ifEmpty { binding.mToolbar.svSearch.queryHint }
            binding.mToolbar.svSearch.setQuery(textToSearch, true)
        }
    }

    private fun updateViewsOnLangChange(fromQuechua: Boolean) {
        val innerViews = mutableListOf(
            binding.spSearchTypes,
            binding.vSeparator,
            binding.tvQichwaLang,
            binding.ivSwapLanguages,
            binding.spDictLangs
        )
        if(!fromQuechua) {
            innerViews.removeAt(4)
            innerViews.removeAt(2)
            innerViews.add(2, binding.spDictLangs)
            innerViews.add(4, binding.tvQichwaLang)
        }
        binding.llSearchOptions.removeAllViews()
        innerViews.forEach { binding.llSearchOptions.addView(it) }
        updateQueryHint(fromQuechua)
    }

    private fun updateQueryHint(fromQuechua: Boolean) {
        if (fromQuechua) {
            binding.mToolbar.svSearch.queryHint = quechuaPlaceholder
        } else {
            val currentLang = (binding.spDictLangs.selectedItem as DictLang).code
            binding.mToolbar.svSearch.queryHint = placeholders[currentLang]
        }
    }

}
