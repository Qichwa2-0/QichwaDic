package com.ocram.qichwadic.features.search.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import com.ocram.qichwadic.core.ui.common.LinearLoadingIndicator
import com.ocram.qichwadic.features.search.ui.components.*
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = getViewModel(),
//    searchUiState: SearchUiState,
    openDrawer: () -> Unit,
    showSnackbar: (message: String, label: String?, action: () -> Unit, onDismiss: () -> Unit) -> Unit,
    goToDictionaries: () -> Unit,
    goToFavorites: () -> Unit,
//    onSearchOfflineChanged: (offline: Boolean) -> Unit,
//    onQueryChanged: (queryText: String) -> Unit,
//    onSearchTypeSelected: (pos: Int) -> Unit,
//    onLanguageSelected: (lang: String) -> Unit,
//    switchFromQuechua: () -> Unit,
//    search: () -> Unit,
//    onSearchResultsItemSelected: (pos: Int) -> Unit,
    shareDefinition: (text: String) -> Unit,
//    saveFavoriteDefinition: (definition: DefinitionModel) -> Unit,
//    resetMessagesStates: () -> Unit,
//    fetchMoreResults: () -> Unit
) {
    val context = LocalContext.current

    val searchUiState = viewModel.uiState

    if (searchUiState.favoriteAdded != FavoriteAdded.NONE) {
        val stringId = if (searchUiState.favoriteAdded == FavoriteAdded.SUCCESS)
            R.string.favorite_added_success
        else R.string.favorite_added_error
        val text  = stringResource(stringId)
        val actionLabel = stringResource(R.string.message_action_view)
        DisposableEffect(searchUiState.favoriteAdded) {
            showSnackbar(text, actionLabel, goToFavorites, viewModel::resetMessageStates)
            onDispose {
                viewModel.resetMessageStates()
            }
        }
    }


    if (searchUiState.searchModeMessage != SearchModeMessage.NONE) {
        DisposableEffect(searchUiState.searchModeMessage) {
            val stringId = if (searchUiState.searchModeMessage == SearchModeMessage.ONLINE) {
                R.string.offline_search_inactive
            } else {
                R.string.offline_search_active
            }
            val text = context.getString(stringId)
            showSnackbar(text, null, {}, {})
            onDispose { viewModel.resetMessageStates() }
        }
    }

    SearchView(
        searchUiState = searchUiState,
        openDrawer = openDrawer,
        goToDictionaries = goToDictionaries,
        shareDefinition = shareDefinition,
        onSearchOfflineChanged = viewModel::onOfflineSearchChanged,
        onQueryChanged = viewModel::onQueryChanged,
        search = viewModel::search,
        onSearchTypeSelected = viewModel::onSearchTypeChanged,
        onLanguageSelected = viewModel::onNonQuechuaLangSelected,
        switchFromQuechua = viewModel::switchIsFromQuechua,
        onSearchResultItemSelected = viewModel::onSearchResultsItemSelected,
        saveFavoriteDefinition = viewModel::saveFavorite,
        viewModel::fetchMoreResults
    )
}

@Composable
fun SearchView(
    searchUiState: SearchUiState,
    openDrawer: () -> Unit,
    goToDictionaries: () -> Unit,
    shareDefinition: (text: String) -> Unit,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
    onQueryChanged: (queryText: String) -> Unit,
    search: () -> Unit,
    onSearchTypeSelected: (pos: Int) -> Unit,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit,
    onSearchResultItemSelected: (pos: Int) -> Unit,
    saveFavoriteDefinition: (definition: DefinitionModel) -> Unit,
    fetchMoreResults: () -> Unit
) {
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
        Crossfade(targetState = searchUiState.searchState) { state ->
            when (state) {
                SearchState.LOADING -> {
                    LinearLoadingIndicator(loadingMessageId = R.string.loading_search)
                }
                SearchState.ERROR -> {
                    SearchResultsError(Modifier.padding(top = 64.dp), searchUiState.offlineSearch)
                }
                SearchState.SUCCESS -> {
                    SearchResults(
                        offline = searchUiState.offlineSearch,
                        searchResults = searchUiState.searchResults,
                        searchResultSelectedPos = searchUiState.searchResultSelectedPos,
                        fetchMoreLoading = searchUiState.fetchMoreLoading,
                        goToDictionaries = goToDictionaries,
                        onItemSelected = onSearchResultItemSelected,
                        shareDefinition = shareDefinition,
                        saveFavoriteDefinition = saveFavoriteDefinition,
                        fetchMoreResults = fetchMoreResults
                    )
                }
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
    searchResultSelectedPos: Int,
    fetchMoreLoading: Boolean,
    goToDictionaries: () -> Unit,
    onItemSelected: (pos: Int) -> Unit,
    shareDefinition: (text: String) -> Unit,
    saveFavoriteDefinition: (definition: DefinitionModel) -> Unit,
    fetchMoreResults: () -> Unit
) {
    if (searchResults.isEmpty()) {
        if (offline) {
            SearchNoResultsOffline(Modifier.padding(top = 64.dp), goToDictionaries)
        } else {
            SearchNoResultsOnline(Modifier.padding(top = 64.dp))
        }
    } else {
        val context = LocalContext.current
        val listState = rememberLazyListState()

        val definitionToShareTemplate = context.getString(R.string.share_definition_from_dictionary)

        val currentResult = searchResults.getOrNull(searchResultSelectedPos)
        Column(Modifier.fillMaxSize()) {
            SearchResultsDropdown(
                searchResults = searchResults,
                currentResult = currentResult,
                onItemSelected = onItemSelected
            )
            currentResult?.let { currentResult ->
                LaunchedEffect(currentResult) {
                    listState.scrollToItem(0)
                }
                Column(Modifier.fillMaxSize()) {

                    if (currentResult.hasMoreDefinitions()) {
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.let {
                            if ((currentResult.definitions.size - it.index) < 5) {
                                fetchMoreResults()
                            }
                        }
                    }
                    Box(Modifier.weight(1F)) {
                        LazyColumn(state = listState) {
                            itemsIndexed(
                                items = currentResult.definitions,
                                key = { _, item -> item.id }
                            ) { _, item ->
                                ResultDefinitionCard(
                                    definition = item,
                                    shareDefinition = {
                                        val textToShare = definitionToShareTemplate.format(
                                            item.dictionaryName,
                                            item.word,
                                            item.meaning
                                        )
                                        shareDefinition(textToShare)
                                    },
                                    saveFavorite = { saveFavoriteDefinition(item) }
                                )
                            }
                        }
                    }
                    if (fetchMoreLoading) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun PreviewSearchScreen() {
    val list = remember {
        mutableStateListOf(
            SearchResultModel(
                dictionaryId = 1,
                dictionaryName = "Dictionary (Minedu)",
                total = 10,
                definitions = (1..10).map {
                    DefinitionModel(
                        id = it,
                        word = "Word $it",
                        meaning = "meaning"
                    )
                }.toMutableList()
            )
        )
    }
    SearchView(
        SearchUiState(
            searchState = SearchState.SUCCESS,
            searchResults = list,
            searchResultSelectedPos = 0
        ),
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