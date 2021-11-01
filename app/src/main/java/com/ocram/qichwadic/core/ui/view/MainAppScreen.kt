package com.ocram.qichwadic.core.ui.view

import android.content.Intent
import android.net.Uri
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import kotlinx.coroutines.launch

private const val marketUrl =
    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

@Composable
fun MainAppScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val navController = rememberNavController()

    val openDrawer = {
        coroutineScope.launch { scaffoldState.drawerState.open() }
    }
    val showSnackbar = { message: String, label: String?, action: () -> Unit, onDismiss: () -> Unit ->
        coroutineScope.launch {
            val result = scaffoldState.snackbarHostState.showSnackbar(message, label)
            if(result == SnackbarResult.ActionPerformed) {
                action()
            } else if (result == SnackbarResult.Dismissed) {
                onDismiss()
            }
        }
    }

    val openShareIntent = { textToShare: String, subject: String? ->
        val textType = "text/plain"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = textType
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, subject) }
        }
        context.startActivity(
            Intent.createChooser(sendIntent, context.getString(R.string.share_with))
        )
    }

    val openActionViewIntent = { uri: String ->
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) }
        context.startActivity(intent)
    }

    val shareAppMsg = remember { context.getString(R.string.share_app_message, marketUrl) }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Drawer(
                onDestinationClicked = { drawerScreen ->
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                    if (drawerScreen is DrawerItem.Share) {
                        openShareIntent(shareAppMsg, null)
                    } else {
                        navController.navigate(drawerScreen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }

                }
            )
        },
        content = {
            AppNavGraph(
                navController = navController,
                openDrawer = { openDrawer() },
                showSnackbar = { text, label, action, onDismiss ->
                    showSnackbar(text, label, action, onDismiss) },
                openShareIntent = openShareIntent,
                openActionWebView = openActionViewIntent
            )
        }
    )
}