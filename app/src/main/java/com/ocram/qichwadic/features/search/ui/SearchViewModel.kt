package com.ocram.qichwadic.features.search.ui

import android.util.Log
import androidx.lifecycle.*

import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.features.search.domain.SearchInteractor
import com.ocram.qichwadic.core.ui.SearchParams
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
        private val searchInteractor: SearchInteractor,
        private val favoriteInteractor: FavoriteInteractor,
        private val preferencesHelper: PreferencesHelper) : ViewModel() {


    val resultLiveData = MutableLiveData<List<SearchResultModel>>()

    val extraDefinitions = MutableLiveData<List<DefinitionModel>>()
    val searchViewState = MutableLiveData<SearchViewState>()
    val loadFetchMore = MutableLiveData<Boolean>()
    val saveFavoriteResult = MutableLiveData<Event<Boolean>>()
    val offlineSearch = MutableLiveData<Boolean>()
    val searchParams = MutableLiveData<Event<SearchParams>>()
    var searchFromQuechua = MutableLiveData<Boolean>()
    private lateinit var _searchParams: SearchParams


    init {
        searchViewState.value = SearchViewState.LOADING
        loadSearchModeConfig()
        loadSearchParams()
    }

    private fun loadSearchModeConfig() {
        viewModelScope.launch {
            offlineSearch.postValue(preferencesHelper.isOfflineSearchMode())
        }
    }

    private fun loadSearchParams() {
        viewModelScope.launch {
            val lastSearchParams = preferencesHelper.lastSearchParams()
            _searchParams = lastSearchParams
            searchParams.postValue(Event(lastSearchParams))
        }
    }

    fun searchWord(word: String) {
        searchViewState.value = SearchViewState.LOADING
        _searchParams.searchWord = word
        viewModelScope.launch {
            preferencesHelper.saveSearchWord(word)
            try {
                val results = searchInteractor.queryWord(preferencesHelper.isOfflineSearchMode(), _searchParams)
                onSearchSuccess(results)
            } catch (e: Throwable) {
                onSearchError(e)
            }
        }
    }

    private fun onSearchSuccess(searchResults: List<SearchResultModel>) {
        searchViewState.value = SearchViewState.SUCCESS
        resultLiveData.postValue(searchResults)
    }

    private fun onSearchError(throwable: Throwable) {
        Log.e("ERROR", "error searching", throwable)
        searchViewState.value = SearchViewState.ERROR
    }

    fun fetchMoreResults(dictionaryId: Int, page: Int) {
        loadFetchMore.value = true
        viewModelScope.launch {
            try {
                val results = searchInteractor.fetchMoreResults(
                        preferencesHelper.isOfflineSearchMode(),
                        dictionaryId,
                        _searchParams.searchTypePos,
                        _searchParams.searchWord,
                        page
                )
                onFetchMoreSuccess(results)

            } catch (e: Throwable) {
                Log.d("QichwaDic", e.message ?: "Error fetching more results")
            }
        }
    }

    private fun onFetchMoreSuccess(definitions: List<DefinitionModel>) {
        loadFetchMore.postValue(false)
        extraDefinitions.postValue(definitions)
    }

    fun saveFavorite(definition: DefinitionModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val added = favoriteInteractor.addFavorite(definition)
                saveFavoriteResult.postValue(Event(added))
            }
        }
    }

    fun changeSearchModeConfig(isOffline: Boolean) {
        viewModelScope.launch {
            preferencesHelper.saveOfflineSearchMode(isOffline)
            offlineSearch.value = isOffline
        }
    }

    fun saveSearchFromQuechua() {
        _searchParams.isFromQuechua = !_searchParams.isFromQuechua
        viewModelScope.launch {
            preferencesHelper.saveSearchFromQuechua(_searchParams.isFromQuechua)
            searchFromQuechua.postValue(_searchParams.isFromQuechua)
        }
    }

    fun saveNonQuechuaLangPos(langCode: String) {
        _searchParams.nonQuechuaLangCode = langCode
        viewModelScope.launch {
            preferencesHelper.saveNonQuechuaLangPos(langCode)
            searchFromQuechua.postValue(_searchParams.isFromQuechua)
        }
    }

    fun saveSearchType(searchType: Int) {
        _searchParams.searchTypePos = searchType
        viewModelScope.launch {
            preferencesHelper.saveSearchType(searchType)
        }
    }
}
