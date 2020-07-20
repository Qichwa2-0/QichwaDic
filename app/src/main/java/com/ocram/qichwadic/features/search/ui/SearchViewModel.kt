package com.ocram.qichwadic.features.search.ui

import android.util.Log
import androidx.lifecycle.*

import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.features.search.domain.SearchInteractor
import com.ocram.qichwadic.core.ui.SearchParams
import com.ocram.qichwadic.core.domain.model.DefinitionModel
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
    val saveFavoriteResult = MutableLiveData<Boolean>()
    val offlineSearch = MutableLiveData<Boolean>()
    val searchParams = MutableLiveData<SearchParams>()


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
            searchParams.postValue(lastSearchParams)
        }
    }

    fun searchWord(fromQuechua: Int, target: String, searchType: Int, word: String) {
        searchViewState.value = SearchViewState.LOADING

        viewModelScope.launch {
            preferencesHelper.saveSearchParams(target, fromQuechua == 1, searchType)
            try {
                val results = searchInteractor.queryWord(preferencesHelper.isOfflineSearchMode(), fromQuechua, target, searchType, word)
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

    fun fetchMoreResults(dictionaryId: Int, searchType: Int, word: String, page: Int) {
        loadFetchMore.value = true
        viewModelScope.launch {
            try {
                val results = searchInteractor.fetchMoreResults(preferencesHelper.isOfflineSearchMode(), dictionaryId, searchType, word, page)
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
                saveFavoriteResult.postValue(added)
            }
        }
    }

    fun changeSearchModeConfig(isOffline: Boolean) {
        viewModelScope.launch {
            preferencesHelper.saveOfflineSearchMode(isOffline)
            offlineSearch.value = isOffline
        }
    }

    fun saveSearchFromQuechua(fromQuechua: Boolean) {
        viewModelScope.launch {
            preferencesHelper.saveSearchFromQuechua(fromQuechua)
        }
    }

    fun saveNonQuechuaLangPos(langCode: String) {
        viewModelScope.launch {
            preferencesHelper.saveNonQuechuaLangPos(langCode)
        }
    }
}
