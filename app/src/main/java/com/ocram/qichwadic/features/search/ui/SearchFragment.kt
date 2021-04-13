package com.ocram.qichwadic.features.search.ui


import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.DictLang
import com.ocram.qichwadic.core.ui.Event
import com.ocram.qichwadic.core.ui.SearchParams
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.math.max

class SearchFragment : Fragment() {

    private lateinit var placeholders: Map<String, String>
    private lateinit var quechuaPlaceholder: String
//    private var isFromQuechua: Boolean = false

    private val searchViewModel: SearchViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (BuildConfig.DEBUGGABLE) {
            this.activateStrictMode()
        }
        loadPlaceholders()
        return inflater.inflate(R.layout.fragment_search, container, false)
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

        ivSwapLanguages.setOnClickListener { swapLanguages() }
        fabSearch.setOnClickListener { runSearch() }

        spDictLangs.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                spDictLangs.selectedItem.let {
                    searchViewModel.saveNonQuechuaLangPos((it as DictLang).code)
                }
            }
        }

        spSearchTypes.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                searchViewModel.saveSearchType(p2)
            }
        }

        swOfflineSearch.setOnCheckedChangeListener { _, checked -> toggleOfflineSearch(checked) }
        ivSubmitSearch.setOnClickListener { runSearch() }

        searchViewModel.searchParams.observe(viewLifecycleOwner, Observer { this.setSearchParams(it) })
        searchViewModel.saveFavoriteResult.observe(viewLifecycleOwner, Observer { this.onSaveWordResult(it) })
        searchViewModel.offlineSearch.observe(viewLifecycleOwner, Observer<Boolean> { this.onSearchModeChanged(it) })
        searchViewModel.searchFromQuechua.observe(viewLifecycleOwner, Observer<Boolean> {
            this.updateViewsOnLangChange(it)
        })
    }

    private fun setAdapters() {
        val searchTypesAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.searchTypes, R.layout.item_spinner_search_type)
        searchTypesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_search_type)
        spSearchTypes.adapter = searchTypesAdapter
        spSearchTypes.setSelection(2)

        spDictLangs.adapter = SearchDictLangAdapter(requireContext())
    }

    private fun initSearchView() {
        svSearch.setQuery(svSearch.queryHint, false)
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(javaClass.name, "onquerysubmit $query")
                searchViewModel.searchWord(query)
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    private fun runSearch() {
        if (svSearch.query.isNotEmpty()) {
            svSearch.setQuery(svSearch.query, true)
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
            Snackbar.make(clSearch, getString(stringId), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.see_favorites)) {
                        findNavController().navigate(R.id.search_fragment_to_favorites_fragment)
                    }.show()
        }
    }

    private fun onSearchModeChanged(isOffline: Boolean?) {
        swOfflineSearch.isChecked = isOffline ?: false
    }

    private fun setSearchParams(searchParams: Event<SearchParams>) {
        searchParams.getContentIfNotHandled()?.let {
            if (!it.isFromQuechua) {
                this.updateViewsOnLangChange(it.isFromQuechua)
            }
            spSearchTypes.setSelection(it.searchTypePos)
            val dictLangPos = (spDictLangs.adapter as SearchDictLangAdapter).getPosByValue(it.nonQuechuaLangCode)
            spDictLangs.setSelection(max(0, dictLangPos))

            updateQueryHint(it.isFromQuechua)
            val textToSearch =
                    if(it.searchWord.isNotEmpty())
                        it.searchWord
                    else svSearch.queryHint
            svSearch.setQuery(textToSearch, true)
        }
    }

    private fun updateViewsOnLangChange(fromQuechua: Boolean) {
        val innerViews = mutableListOf(spSearchTypes, vSeparator, tvQichwaLang, ivSwapLanguages, spDictLangs)
        if(!fromQuechua) {
            innerViews.removeAt(4)
            innerViews.removeAt(2)
            innerViews.add(2, spDictLangs)
            innerViews.add(4, tvQichwaLang)
        }
        llSearchOptions.removeAllViews()
        innerViews.forEach { llSearchOptions.addView(it) }
        updateQueryHint(fromQuechua)
    }

    private fun updateQueryHint(fromQuechua: Boolean) {
        if (fromQuechua) {
            svSearch.queryHint = quechuaPlaceholder
        } else {
            val currentLang = (spDictLangs.selectedItem as DictLang).code
            svSearch.queryHint = placeholders[currentLang]
        }
    }

}
