package com.ocram.qichwadic.features.search.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.R
import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.domain.SearchResultModel
import com.ocram.qichwadic.features.about.ui.AboutActivity
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryActivity
import com.ocram.qichwadic.features.favorites.ui.FavoriteActivity
import com.ocram.qichwadic.features.search.ui.custom.EndlessRecyclerViewScrollListener
import com.ocram.qichwadic.features.common.DictLang
import com.ocram.qichwadic.features.common.SearchParams
import com.ocram.qichwadic.core.ui.activity.BaseActivity
import com.ocram.qichwadic.features.common.domain.DefinitionModel

import java.util.ArrayList

import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.max

class SearchActivity : BaseActivity(), SearchResultListAdapter.DefinitionClickListener {

    private lateinit var spinnerResultAdapter: SpinnerResultAdapter
    private lateinit var searchResultListAdapter: SearchResultListAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var searchType: Int = 0
    private lateinit var placeholders: Map<String, String>
    private lateinit var quechuaPlaceholder: String
    private var isFromQuechua: Boolean = false
    private var searchParams: SearchParams? = null

    private val searchViewModel: SearchViewModel by viewModel()
    private var offlineSearch: Boolean = false

    override val layoutId: Int = R.layout.activity_main

    override fun getToolbar(): Toolbar? {
        return mToolbar as Toolbar?
    }

    private lateinit var currentLang: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPlaceholders()
        setAdapters()
        initSearchView()
        currentLang = (spDictLangs.selectedItem as DictLang).code
        searchViewModel.resultLiveData.observe(this, Observer<List<SearchResultModel>> { this.onResultsChanged(it) })
        searchViewModel.searchViewState.observe(this, Observer<SearchViewState> { this.onSearchViewStateChanged(it) })
        searchViewModel.extraDefinitions.observe(this, Observer<List<DefinitionModel>> { this.onFetchExtraDefinitions(it) })
        searchViewModel.loadFetchMore.observe(this, Observer<Boolean> { this.onFetchMoreLoadingChanged(it) })
        searchViewModel.saveFavoriteResult.observe(this, Observer<Boolean> { this.onSaveWordResult(it) })
        searchViewModel.offlineSearch.observe(this, Observer<Boolean> { this.onSearchModeChanged(it) })
        searchViewModel.searchParams.observe(this, Observer<SearchParams> { this.setSearchParams(it) })
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

    private fun setAdapters() {
        val searchTypesAdapter = ArrayAdapter.createFromResource(this, R.array.searchTypes, R.layout.item_spinner_search_type)
        searchTypesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_search_type)
        spSearchTypes.adapter = searchTypesAdapter
        spSearchTypes.setSelection(2)

        spDictLangs.adapter = SearchDictLangAdapter(this)
        spinnerResultAdapter = SpinnerResultAdapter(this, R.layout.item_spinner_result, ArrayList())
        searchResultListAdapter = SearchResultListAdapter(ArrayList(), this)
        rvResults.adapter = searchResultListAdapter
        spResults.adapter = spinnerResultAdapter
    }

    private fun initSearchView() {
        svSearch.setQuery(svSearch.queryHint, false)
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(javaClass.name, "onquerysubmit $query")
                searchViewModel.searchWord(if (isFromQuechua) 1 else 0, (spDictLangs.selectedItem as DictLang).code, spSearchTypes.selectedItemPosition, query)
                svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_dictionaries -> {
                openDictionariesGallery()
                return true
            }
            R.id.nav_favorites -> {
                val intent = Intent(this@SearchActivity, FavoriteActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_about -> {
                val intent = Intent(this@SearchActivity, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun initViews() {
        btnNoResultsGetDictionaries.setOnClickListener { openDictionariesGallery() }
        ivSwapLanguages.setOnClickListener { swapLanguages() }
        fabSearch.setOnClickListener { runSearch() }
        spResults.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                onItemSelected(pos)
            }
        }

        spDictLangs.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                onDictLangSelected()
            }

        }

        swOfflineSearch.setOnCheckedChangeListener { _, checked -> onSearchOfflineModeChanged(checked) }
        ivSubmitSearch.setOnClickListener { runSearch() }
        setToolbar()
        setRecyclerViews()
    }

    private fun openDictionariesGallery() {
        val intent = Intent(this@SearchActivity, DictionaryActivity::class.java)
        startActivity(intent)
    }

    private fun runSearch() {
        if (svSearch.query.isNotEmpty()) {
            svSearch.setQuery(svSearch.query, true)
        }
    }

    private fun swapLanguages() {
        this.isFromQuechua = !this.isFromQuechua
        this.updateViewsOnLangChange()
        searchViewModel.saveSearchFromQuechua(this.isFromQuechua)
    }

    private fun onItemSelected(pos: Int) {
        Log.d(javaClass.name, "item selected $pos")
        val result = spResults.getItemAtPosition(pos) as SearchResultModel
        selectResult(result)
    }

    private fun onDictLangSelected() {
        updateQueryHint()
        spDictLangs.selectedItem.let {
            searchViewModel.saveNonQuechuaLangPos((it as DictLang).code)
        }
    }

    private fun onSearchOfflineModeChanged(checked: Boolean) {
        searchViewModel.changeSearchModeConfig(checked)
    }

    private fun setToolbar() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun setRecyclerViews() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvResults.layoutManager = layoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                val currentSearchResult = spResults.selectedItem as SearchResultModel
                val totalToFetch = currentSearchResult.total
                if (totalToFetch > 20 && searchResultListAdapter.itemCount < totalToFetch) {
                    val dictionaryId = currentSearchResult.dictionaryId
                    searchViewModel.fetchMoreResults(dictionaryId, searchType, svSearch.query.toString(), page + 1)
                }
            }
        }
        rvResults.addOnScrollListener(scrollListener)
    }

    override fun onItemShareClick(definition: DefinitionModel) {
        val text = getString(R.string.share_definition_from_dictionary,
                definition.dictionaryName, definition.word, definition.meaning)
        openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
    }

    override fun onItemFavoriteClick(definition: DefinitionModel) {
        searchViewModel.saveFavorite(definition)
    }

    private fun setSearchParams(searchParams: SearchParams) {
        this.searchParams = searchParams
        this.isFromQuechua = searchParams.isFromQuechua
        if (!this.isFromQuechua) {
            this.updateViewsOnLangChange()
        }
        spSearchTypes.setSelection(searchParams.searchTypePos)
        val dictLangPos = searchParams.nonQuechuaLangCode?.let { (spDictLangs.adapter as SearchDictLangAdapter).getPosByValue(it) }
        dictLangPos?.let { max(0, it) }?.let { spDictLangs.setSelection(it) }
        updateQueryHint()
        svSearch.setQuery(svSearch.queryHint, true)
    }

    private fun updateViewsOnLangChange() {
        val innerViews = mutableListOf(spSearchTypes, vSeparator, tvQichwaLang, ivSwapLanguages, spDictLangs)
        if(!isFromQuechua) {
            innerViews.removeAt(4)
            innerViews.removeAt(2)
            innerViews.add(2, spDictLangs)
            innerViews.add(4, tvQichwaLang)
        }
        llSearchOptions.removeAllViews()
        innerViews.forEach { llSearchOptions.addView(it) }
        updateQueryHint()
    }

    private fun updateQueryHint() {
        if (isFromQuechua) {
            svSearch.queryHint = quechuaPlaceholder
        } else {
            this.currentLang = (spDictLangs.selectedItem as DictLang).code
            svSearch.queryHint = placeholders[this.currentLang]
        }
    }

    private fun onResultsChanged(searchResults: List<SearchResultModel>?) {
        llResultsArea.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        if (searchResults == null || searchResults.isEmpty()) {
            spinnerResultAdapter.setSearchResults(emptyList())
            searchResultListAdapter.setDefinitions(mutableListOf())
            spResults.visibility = View.GONE
            tvResultsTotal.visibility = View.GONE
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
            tvResultsTotal.visibility = View.VISIBLE
            renderResults(searchResults)
        }
    }

    private fun onSearchViewStateChanged(searchViewState: SearchViewState) {
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

    private fun onFetchExtraDefinitions(definitions: List<DefinitionModel>?) {
        if (!definitions.isNullOrEmpty()) {
            val currentLength = searchResultListAdapter.itemCount
            searchResultListAdapter.pushMoreDefinitions(definitions)
            searchResultListAdapter.notifyItemRangeInserted(currentLength, definitions.size)
        }
    }

    private fun onFetchMoreLoadingChanged(isLoading: Boolean?) {
        fetchMoreProgressBar.visibility = if (isLoading == null || !isLoading) View.GONE else View.VISIBLE
    }

    private fun onSaveWordResult(isSuccess: Boolean?) {
        val stringId = if (isSuccess != null && isSuccess) R.string.favorite_added_success else R.string.favorite_added_error
        Snackbar.make(clSearch, getString(stringId), Snackbar.LENGTH_SHORT).show()
    }

    private fun onSearchModeChanged(isOffline: Boolean?) {
        this.offlineSearch = isOffline != null && isOffline
        swOfflineSearch.isChecked = this.offlineSearch
        this.invalidateOptionsMenu()
    }

    private fun renderResults(searchResults: List<SearchResultModel>) {
        searchType = spSearchTypes.selectedItemPosition
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
        scrollListener.resetState()
        rvResults.visibility = View.VISIBLE
        tvResultsTotal.text = resources.getQuantityString(R.plurals.total_results, result.total,
                result.total)
        searchResultListAdapter.setDefinitions(result.definitions)
        searchResultListAdapter.notifyDataSetChanged()
    }

}
