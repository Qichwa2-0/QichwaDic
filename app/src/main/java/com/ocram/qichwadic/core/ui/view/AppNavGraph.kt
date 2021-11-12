package com.ocram.qichwadic.core.ui.view

import androidx.compose.runtime.Composable
import androidx.core.text.parseAsHtml
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ocram.qichwadic.features.about.AboutScreen
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryScreen
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryViewModel
import com.ocram.qichwadic.features.favorites.ui.FavoriteScreen
import com.ocram.qichwadic.features.favorites.ui.FavoriteViewModel
import com.ocram.qichwadic.features.help.ui.SearchHelpScreen
import com.ocram.qichwadic.features.search.ui.SearchScreen
import com.ocram.qichwadic.features.search.ui.SearchViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    openDrawer: () -> Unit,
    showSnackbar: (message: String, label: String?, action: () -> Unit, onDismiss: () -> Unit) -> Unit,
    openShareIntent: (textToShare: String, subject: String?) -> Unit,
    openActionWebView: (uri: String) -> Unit
) {
    val onBackPressed = { navController.popBackStack() }
    val shareHtmlText = { text: String -> openShareIntent(text.parseAsHtml().toString(), null) }
    NavHost(
        navController = navController,
        startDestination = DrawerItem.Home.route
    ) {
        composable(DrawerItem.Home.route) {
            val viewModel = getViewModel<SearchViewModel>()
            SearchScreen(
                searchUiState = viewModel.uiState,
                searchResults = viewModel.searchResults,
                openDrawer = openDrawer,
                showSnackbar = showSnackbar,
                goToFavorites = { navController.navigate(DrawerItem.Favorites.route) },
                goToDictionaries = { navController.navigate(DrawerItem.Dictionaries.route) },
                onSearchOfflineChanged = viewModel::onOfflineSearchChanged,
                onQueryChanged = viewModel::onQueryChanged,
                onSearchTypeSelected = viewModel::onSearchTypeChanged,
                onLanguageSelected = viewModel::onNonQuechuaLangSelected,
                switchFromQuechua = viewModel::switchIsFromQuechua,
                search = viewModel::searchWord,
                onSearchResultsItemSelected = viewModel::onSearchResultsItemSelected,
                shareDefinition = { shareHtmlText(it) },
                saveFavoriteDefinition = viewModel::saveFavorite,
                resetMessagesStates = viewModel::resetMessageStates,
                fetchMoreResults = viewModel::fetchMoreResults
            )
        }
        composable(DrawerItem.Dictionaries.route) {
            val viewModel = getViewModel<DictionaryViewModel>()
            DictionaryScreen(
                uiState = viewModel.dictionaryUiState,
                dictionaries = viewModel.allDictionaries,
                showSnackbar = { message -> showSnackbar(message, null, {}) {} },
                onBackPressed = { onBackPressed() },
                onDictionaryLanguageSelected = viewModel::onLanguageSelected,
                onDownloadClicked = viewModel::onDownloadClicked
            )
        }
        composable(DrawerItem.Help.route) {
            SearchHelpScreen (
                onBackPressed = { onBackPressed() },
                openActionWebview = openActionWebView
            )
        }
        composable(DrawerItem.Favorites.route) {
            val viewModel = getViewModel<FavoriteViewModel>()
            FavoriteScreen(
                deletedFavoriteState = viewModel.deletedFavoriteState,
                loading = viewModel.loading,
                favorites = viewModel.favorites,
                onBackPressed = { onBackPressed() },
                showSnackbar = { message, onDismiss -> showSnackbar(message, null, {}, onDismiss) },
                deleteAll = viewModel::clearFavorites,
                share = { shareHtmlText(it) },
                deleteOne = viewModel::removeFavorite,
                resetDeleteStates = viewModel::resetDeleteStates
            )
        }
        composable(DrawerItem.About.route) {
            AboutScreen(
                onBackPressed = { onBackPressed() },
                openActionWebview = openActionWebView
            )
        }
    }
}