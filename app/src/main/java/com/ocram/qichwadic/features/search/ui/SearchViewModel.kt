package com.ocram.qichwadic.features.search.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue

import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.features.search.domain.SearchInteractor
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import kotlinx.coroutines.launch

enum class SearchState { LOADING, ERROR, SUCCESS }
enum class FavoriteAdded { NONE, SUCCESS, ERROR }
enum class InternetSearchState { NONE, ONLINE, OFFLINE }

private val placeholders = mapOf(
    "qu" to "Yawar",
    "es" to "Amanecer",
    "en" to "House",
    "fr" to "Pierre",
    "de" to "Stuhl",
    "it" to "Giocare",
    "ru" to "играть"
)

data class SearchUiState(
    var placeholder: String = placeholders.values.first(),
    var searchParams: SearchParams = SearchParams(),
    var offlineSearch: Boolean = false,
    var internetSearchState: InternetSearchState = InternetSearchState.NONE,
    var searchState: SearchState = SearchState.LOADING,
    var searchResultSelectedPos: Int = 0,
    var fetchMoreLoading: Boolean = false,
    var favoriteAdded: FavoriteAdded = FavoriteAdded.NONE
)

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val defaultPlaceholder = placeholders.values.first()

    var uiState by mutableStateOf(SearchUiState())

    var searchResults = mutableStateListOf<SearchResultModel>()
        private set

    private var _searchOffline = false

    init {
        loadSearchModeConfig()
        loadSearchParams()
        searchWord()
    }

    private fun loadSearchModeConfig() {
        viewModelScope.launch {
            val isOffline = preferencesHelper.isOfflineSearchMode()
            uiState = uiState.copy(offlineSearch = isOffline)
            _searchOffline = isOffline
        }
    }

    private fun loadSearchParams() {
        viewModelScope.launch {
            val lastSearchParams = preferencesHelper.lastSearchParams()
            uiState = uiState.copy(searchParams = lastSearchParams)
            updatePlaceholder()
            if(uiState.searchParams.searchWord.isEmpty()) {
                uiState = uiState.copy(
                    searchParams = uiState.searchParams.copy(searchWord = uiState.placeholder)
                )
            }
        }
    }

    fun onQueryChanged(queryText: String) {
        uiState = uiState.copy(searchParams = uiState.searchParams.copy(searchWord = queryText))
    }

    fun onSearchTypeChanged(searchTypePos: Int) {
        viewModelScope.launch {
            preferencesHelper.saveSearchType(searchTypePos)
            uiState = uiState.copy(searchParams = uiState.searchParams.copy(searchTypePos = searchTypePos))
        }
    }

    fun onNonQuechuaLangSelected(lang: String) {
        viewModelScope.launch {
            preferencesHelper.saveNonQuechuaLangPos(lang)
            uiState = uiState.copy(searchParams = uiState.searchParams.copy(nonQuechuaLangCode = lang))
            updatePlaceholder()
        }
    }

    fun switchIsFromQuechua() {
        viewModelScope.launch {
            val newVal = !uiState.searchParams.isFromQuechua
            preferencesHelper.saveSearchFromQuechua(newVal)
            uiState = uiState.copy(searchParams = uiState.searchParams.copy(isFromQuechua = newVal))
            updatePlaceholder()
        }
    }

    private fun updatePlaceholder() {
        val placeholder = if(uiState.searchParams.isFromQuechua) {
            defaultPlaceholder
        } else {
            placeholders[uiState.searchParams.nonQuechuaLangCode] ?: defaultPlaceholder
        }
        uiState = uiState.copy(placeholder = placeholder)
    }

    fun onOfflineSearchChanged(offline: Boolean) {
        this._searchOffline = offline
        val onlineSearchState = if (offline) InternetSearchState.OFFLINE else InternetSearchState.ONLINE
        uiState = uiState.copy(internetSearchState = onlineSearchState)
    }

    fun searchWord() {
        if(enableSearch()) {
            uiState = uiState.copy(searchState = SearchState.LOADING, searchResultSelectedPos = 0)
            viewModelScope.launch {
                preferencesHelper.saveSearchWord(uiState.searchParams.searchWord)
                try {
                    val results = searchInteractor.queryWord(
                        _searchOffline,
                        uiState.searchParams
                    )
                    onSearchSuccess(results)
                } catch (e: Throwable) {
                    onSearchError()
                } finally {
                    preferencesHelper.saveSearchWord(uiState.searchParams.searchWord)
                    preferencesHelper.saveOfflineSearchMode(_searchOffline)
                    loadSearchModeConfig()
                }
            }
        }
    }

    private fun enableSearch(): Boolean = uiState.searchParams.searchWord.isNotBlank()

    private fun onSearchSuccess(searchResults: List<SearchResultModel>) {
        uiState = uiState.copy(searchState = SearchState.SUCCESS)
        this.searchResults.apply {
            clear()
            addAll(searchResults)
        }
    }

    private fun onSearchError() {
        uiState = uiState.copy(searchState = SearchState.ERROR)
    }

    fun onSearchResultsItemSelected(pos: Int) {
        uiState = uiState.copy(searchResultSelectedPos = pos)
    }

    fun resetFavoriteAddedState() {
        uiState = uiState.copy(favoriteAdded = FavoriteAdded.NONE)
    }

    fun fetchMoreResults() {
        if (searchResults[uiState.searchResultSelectedPos].total
            <= searchResults[uiState.searchResultSelectedPos].definitions.size ) {
            return
        }
        if(uiState.fetchMoreLoading) {
            return
        }
        uiState = uiState.copy(fetchMoreLoading = true)
        viewModelScope.launch {
            try {
                val results = searchInteractor.fetchMoreResults(
                    uiState.offlineSearch,
                    searchResults[uiState.searchResultSelectedPos].dictionaryId,
                    uiState.searchParams.searchTypePos,
                    uiState.searchParams.searchWord,
                    (searchResults[uiState.searchResultSelectedPos].definitions.size / 20) + 1
                )
                searchResults[uiState.searchResultSelectedPos].definitions.addAll(results)
            } catch (e: Throwable) {
                Log.d("QichwaDic", e.message ?: "Error fetching more results")
            } finally {
                uiState = uiState.copy(fetchMoreLoading = false)
            }
        }
    }

    fun saveFavorite(definition: DefinitionModel) {
        viewModelScope.launch {
            val added = favoriteInteractor.addFavorite(definition)
            uiState = uiState.copy(
                favoriteAdded = if (added) FavoriteAdded.SUCCESS else FavoriteAdded.ERROR
            )
        }
    }
}
