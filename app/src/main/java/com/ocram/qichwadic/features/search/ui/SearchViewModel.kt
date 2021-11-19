package com.ocram.qichwadic.features.search.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.compose.runtime.snapshots.SnapshotStateList

import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.features.search.domain.SearchInteractor
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import kotlinx.coroutines.launch

enum class SearchState { LOADING, ERROR, SUCCESS }
enum class FavoriteAdded { NONE, SUCCESS, ERROR }
enum class SearchModeMessage { NONE, ONLINE, OFFLINE }

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
    val placeholder: String = placeholders.values.first(),
    val searchParams: SearchParams = SearchParams(),
    val offlineSearch: Boolean = false,
    val searchModeMessage: SearchModeMessage = SearchModeMessage.NONE,
    val searchState: SearchState = SearchState.LOADING,
    val searchResults: SnapshotStateList<SearchResultModel> = mutableStateListOf(),
    val searchResultSelectedPos: Int = -1,
    val fetchMoreLoading: Boolean = false,
    val favoriteAdded: FavoriteAdded = FavoriteAdded.NONE
)

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val defaultPlaceholder = placeholders.values.first()

    var uiState by mutableStateOf(SearchUiState())

    private val enableSearch get(): Boolean = uiState.searchParams.searchWord.isNotBlank()

    init {
        loadSearchModeConfig()
        loadSearchParams()
        search()
    }

    private fun loadSearchModeConfig() {
        viewModelScope.launch {
            val isOffline = preferencesHelper.isOfflineSearchMode()
            setState { copy(offlineSearch = isOffline) }
        }
    }

    private fun loadSearchParams() {
        viewModelScope.launch {
            val lastSearchParams = preferencesHelper.lastSearchParams()
            setState { copy(searchParams = lastSearchParams) }
            updatePlaceholder()
            if(uiState.searchParams.searchWord.isEmpty()) {
                setState {
                    copy(searchParams = uiState.searchParams.copy(searchWord = uiState.placeholder))
                }
            }
        }
    }

    fun onQueryChanged(queryText: String) {
        setState { copy(searchParams = uiState.searchParams.copy(searchWord = queryText)) }
    }

    fun onSearchTypeChanged(searchTypePos: Int) {
        viewModelScope.launch {
            preferencesHelper.saveSearchType(searchTypePos)
            setState {
                copy(searchParams = uiState.searchParams.copy(searchTypePos = searchTypePos))
            }
        }
    }

    fun onNonQuechuaLangSelected(lang: String) {
        viewModelScope.launch {
            preferencesHelper.saveNonQuechuaLangPos(lang)
            setState { copy(searchParams = uiState.searchParams.copy(nonQuechuaLangCode = lang)) }
            updatePlaceholder()
        }
    }

    fun switchIsFromQuechua() {
        viewModelScope.launch {
            val newVal = !uiState.searchParams.isFromQuechua
            preferencesHelper.saveSearchFromQuechua(newVal)
            setState { copy(searchParams = uiState.searchParams.copy(isFromQuechua = newVal)) }
            updatePlaceholder()
        }
    }

    private fun updatePlaceholder() {
        val placeholder = if(uiState.searchParams.isFromQuechua) {
            defaultPlaceholder
        } else {
            placeholders[uiState.searchParams.nonQuechuaLangCode] ?: defaultPlaceholder
        }
        setState { copy(placeholder = placeholder) }
    }

    fun onOfflineSearchChanged(offline: Boolean) {
        preferencesHelper.saveOfflineSearchMode(offline)
        loadSearchModeConfig()
        val onlineSearchState = if (uiState.offlineSearch) {
            SearchModeMessage.OFFLINE
        } else {
            SearchModeMessage.ONLINE
        }
        setState { copy(searchModeMessage = onlineSearchState) }
    }

    fun search() {
        if(enableSearch) {
            setState {
                copy(searchState = SearchState.LOADING)
            }
            viewModelScope.launch {
                preferencesHelper.saveSearchWord(uiState.searchParams.searchWord)
                try {
                    val newResults = searchInteractor.queryWord(uiState.searchParams)
                    setState {
                        copy(
                            searchState = SearchState.SUCCESS,
                            searchResultSelectedPos = 0,
                            searchResults = newResults.toMutableStateList()
                        )
                    }
                } catch (e: Throwable) {
                    setState { copy(searchState = SearchState.ERROR) }
                } finally {
                    preferencesHelper.saveSearchWord(uiState.searchParams.searchWord)
                }
            }
        }
    }

    fun onSearchResultsItemSelected(pos: Int) = setState { copy(searchResultSelectedPos = pos) }

    fun resetMessageStates() = setState {
        copy(favoriteAdded = FavoriteAdded.NONE, searchModeMessage = SearchModeMessage.NONE)
    }

    fun fetchMoreResults() {
        uiState.searchResults.getOrNull(uiState.searchResultSelectedPos)?.let {
            if (it.total > it.definitions.size && !uiState.fetchMoreLoading) {
                setState { copy(fetchMoreLoading = true) }
                viewModelScope.launch {
                    try {
                        val results = searchInteractor.fetchMoreResults(
                            uiState.offlineSearch,
                            it.dictionaryId,
                            uiState.searchParams.searchTypePos,
                            uiState.searchParams.searchWord,
                            (it.definitions.size / 20) + 1
                        )
                        it.definitions.addAll(results)
                    } catch (e: Throwable) {
                        Log.d("QichwaDic", e.message ?: "Error fetching more results")
                    } finally {
                        setState { copy(fetchMoreLoading = false) }
                    }
                }
            }
        }
    }

    fun saveFavorite(definition: DefinitionModel) {
        viewModelScope.launch {
            val added = favoriteInteractor.addFavorite(definition)
            setState {
                copy(favoriteAdded = if (added) FavoriteAdded.SUCCESS else FavoriteAdded.ERROR)
            }
        }
    }

    private fun setState(applyState: SearchUiState.() -> SearchUiState) {
        uiState = uiState.applyState()
    }
}
