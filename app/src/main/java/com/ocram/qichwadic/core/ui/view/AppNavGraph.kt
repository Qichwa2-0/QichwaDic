package com.ocram.qichwadic.core.ui.view

import androidx.compose.runtime.Composable
import androidx.core.text.parseAsHtml
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ocram.qichwadic.features.about.AboutScreen
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryScreen
import com.ocram.qichwadic.features.favorites.ui.FavoriteScreen
import com.ocram.qichwadic.features.help.ui.SearchHelpScreen
import com.ocram.qichwadic.features.search.ui.SearchScreen

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
            SearchScreen(
                openDrawer = openDrawer,
                showSnackbar = showSnackbar,
                goToFavorites = { navController.navigate(DrawerItem.Favorites.route) },
                goToDictionaries = { navController.navigate(DrawerItem.Dictionaries.route) },
                shareDefinition = { shareHtmlText(it) },
            )
        }
        composable(DrawerItem.Dictionaries.route) {
            DictionaryScreen(
                showSnackbar = { message -> showSnackbar(message, null, {}) {} },
                onBackPressed = { onBackPressed() },
            )
        }
        composable(DrawerItem.Help.route) {
            SearchHelpScreen (
                onBackPressed = { onBackPressed() },
                openActionWebview = openActionWebView
            )
        }
        composable(DrawerItem.Favorites.route) {
            FavoriteScreen(
                onBackPressed = { onBackPressed() },
                showSnackbar = { message, onDismiss -> showSnackbar(message, null, {}, onDismiss) },
                share = { shareHtmlText(it) },
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