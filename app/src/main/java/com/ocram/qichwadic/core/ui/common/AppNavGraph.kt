package com.ocram.qichwadic.core.ui.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ocram.qichwadic.R
import com.ocram.qichwadic.features.about.ui.AboutScreen
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryScreen
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryViewModel
import com.ocram.qichwadic.features.favorites.ui.FavoriteScreen
import com.ocram.qichwadic.features.favorites.ui.FavoriteViewModel
import com.ocram.qichwadic.features.help.ui.SearchHelpScreen
import com.ocram.qichwadic.features.intro.ui.SplashScreen
import com.ocram.qichwadic.features.intro.ui.SplashViewModel
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
    NavHost(
        navController = navController,
        startDestination = DrawerItem.Splash.route
    ) {
        composable(DrawerItem.Splash.route) {
            val viewModel = getViewModel<SplashViewModel>()
            SplashScreen(isFirstStart = viewModel.firstStart) {
                navController.navigate(DrawerItem.Home.route,) {
                    popUpTo(DrawerItem.Splash.route) { inclusive = true }
                    anim {
                        enter = R.anim.enteranim
                        exit = R.anim.exitanim
                    }
                }
            }
        }
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
                shareDefinition = { openShareIntent(it, null) },
                saveFavoriteDefinition = viewModel::saveFavorite,
                resetFavoriteAdded = viewModel::resetFavoriteAddedState,
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
                favorites = viewModel.favorites,
                onBackPressed = { onBackPressed() },
                showSnackbar = { message, onDismiss -> showSnackbar(message, null, {}, onDismiss) },
                deleteAll = viewModel::clearFavorites,
                share = { openShareIntent(it, null) },
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