package com.ocram.qichwadic.features.search.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import com.ocram.qichwadic.core.ui.common.InfiniteScrollList
import com.ocram.qichwadic.core.ui.common.LoadingIndicator
import com.ocram.qichwadic.features.search.ui.components.*

@Composable
fun SearchScreen(
    searchUiState: SearchUiState,
    searchResults: List<SearchResultModel>,
    openDrawer: () -> Unit,
    showSnackbar: (message: String, label: String?, action: () -> Unit, onDismiss: () -> Unit) -> Unit,
    goToDictionaries: () -> Unit,
    goToFavorites: () -> Unit,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
    onQueryChanged: (queryText: String) -> Unit,
    onSearchTypeSelected: (pos: Int) -> Unit,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit,
    search: () -> Unit,
    onSearchResultsItemSelected: (pos: Int) -> Unit,
    shareDefinition: (text: String) -> Unit,
    saveFavoriteDefinition: (definition: DefinitionModel) -> Unit,
    resetFavoriteAdded: () -> Unit,
    fetchMoreResults: () -> Unit
) {
    if (searchUiState.favoriteAdded != FavoriteAdded.NONE) {
        val stringId = if (searchUiState.favoriteAdded == FavoriteAdded.SUCCESS)
            R.string.favorite_added_success
        else R.string.favorite_added_error
        val text  = stringResource(stringId)
        val actionLabel = stringResource(R.string.message_action_view)
        DisposableEffect(searchUiState.favoriteAdded) {
            showSnackbar(text, actionLabel, goToFavorites, resetFavoriteAdded)
            onDispose {
                resetFavoriteAdded()
            }
        }
    }
    if (searchUiState.internetSearchState != InternetSearchState.NONE) {
        val stringId = if (searchUiState.internetSearchState == InternetSearchState.ONLINE)
            R.string.online_search_active
        else R.string.offline_search_active
        val text = stringResource(id = stringId)
        LaunchedEffect(searchUiState.internetSearchState) {
            showSnackbar(text, null, {}, {})
        }
    }
    Column(Modifier.fillMaxSize()) {
        SearchBars(
            openDrawer,
            searchUiState.placeholder,
            searchUiState.searchParams,
            searchUiState.offlineSearch,
            onSearchOfflineChanged,
            onQueryChanged,
            search,
            onSearchTypeSelected,
            onLanguageSelected,
            switchFromQuechua
        )
        when (searchUiState.searchState) {
            SearchState.LOADING -> { LoadingIndicator() }
            SearchState.ERROR -> {
                SearchResultsError(Modifier.padding(top = 64.dp), searchUiState.offlineSearch)
            }
            SearchState.SUCCESS -> {
                SearchResults(
                    offline = searchUiState.offlineSearch,
                    searchResults = searchResults,
                    selectedItemPos = searchUiState.searchResultSelectedPos,
                    fetchMoreLoading = searchUiState.fetchMoreLoading,
                    goToDictionaries = goToDictionaries,
                    onItemSelected = onSearchResultsItemSelected,
                    shareDefinition = shareDefinition,
                    saveFavoriteDefinition = saveFavoriteDefinition,
                    fetchMoreResults = fetchMoreResults
                )
            }
        }
    }
}

@Composable
fun SearchBars(
    openDrawer: () -> Unit,
    placeholder:  String,
    searchParamsState: SearchParams,
    offlineSearch: Boolean,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
    onQueryChanged: (queryText: String) -> Unit,
    search: () -> Unit,
    onSearchTypeSelected: (pos: Int) -> Unit,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit,
) {
    TextSearchBar(
        openDrawer = openDrawer,
        placeholder = placeholder,
        searchText = searchParamsState.searchWord,
        offlineSearch = offlineSearch,
        onSearchOfflineChanged = onSearchOfflineChanged,
        onTextChange = onQueryChanged,
        search = search,
    )
    SearchOptionsBar(
        selectedItemPos = searchParamsState.searchTypePos,
        selectedLanguage = searchParamsState.nonQuechuaLangCode,
        fromQuechua = searchParamsState.isFromQuechua,
        onSearchTypeSelected = onSearchTypeSelected,
        onLanguageSelected = onLanguageSelected,
        switchFromQuechua = switchFromQuechua
    )
}

@Composable
fun SearchResults(
    offline: Boolean,
    searchResults: List<SearchResultModel>,
    selectedItemPos: Int,
    fetchMoreLoading: Boolean,
    goToDictionaries: () -> Unit,
    onItemSelected: (pos: Int) -> Unit,
    shareDefinition: (text: String) -> Unit,
    saveFavoriteDefinition: (definition: DefinitionModel) -> Unit,
    fetchMoreResults: () -> Unit
) {
    if(searchResults.isNotEmpty()) {
        val context = LocalContext.current
        val listState = rememberLazyListState()

        if (searchResults[selectedItemPos].definitions.size - listState.firstVisibleItemIndex < 5) {
            fetchMoreResults()
        }
        LaunchedEffect(selectedItemPos) {
            listState.scrollToItem(0)
        }
        SearchResultsDropdown(
            searchResults = searchResults,
            selectedItemPos = selectedItemPos,
            onItemSelected = onItemSelected
        )
        InfiniteScrollList(
            items = searchResults[selectedItemPos].definitions,
            computeKey = { it.id },
            state = listState,
            fetchMoreLoading = fetchMoreLoading,
        ) {
            ResultDefinitionCard(
                definition = it,
                shareDefinition = {
                    val textToShare = context.getString(
                        R.string.share_definition_from_dictionary,
                        it.dictionaryName,
                        it.word,
                        it.meaning
                    )
                    shareDefinition(textToShare)
                },
                saveFavorite = { saveFavoriteDefinition(it) }
            )
        }
    } else {
        if (offline) {
            SearchNoResultsOffline(Modifier.padding(top = 64.dp), goToDictionaries)
        } else {
            SearchNoResultsOnline(Modifier.padding(top = 64.dp))
        }
    }
}

@Composable
@Preview
fun PreviewSearchScreen() {
    SearchScreen(
        SearchUiState(searchState = SearchState.SUCCESS),
        listOf(
            SearchResultModel(
                dictionaryId = 1,
                dictionaryName = "Dictionary (Minedu)",
                total = 10,
                definitions = mutableListOf(
                    DefinitionModel(
                        id = 1,
                        word = "Word",
                        meaning = "meaning"
                    )
                )
            )
        ),
        {},
        { _, _, _, _ -> },
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {}
    )
}