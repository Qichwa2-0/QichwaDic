package com.ocram.qichwadic.core.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.AppNavGraph
import com.ocram.qichwadic.core.ui.common.Drawer
import com.ocram.qichwadic.core.ui.common.DrawerItem
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val marketUrl = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MdcTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val scaffoldState = rememberScaffoldState(
            rememberDrawerState(initialValue = DrawerValue.Closed)
        )
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
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

        val shareAppMsg = stringResource(R.string.share_app_message, marketUrl)
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                Drawer(
                    onDestinationClicked = { drawerScreen ->
                        coroutineScope.launch { scaffoldState.drawerState.close() }
                        if (drawerScreen is DrawerItem.Share) {
                            openShareIntent(shareAppMsg)
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
                    openShareIntent = this::openShareIntent,
                    openActionWebView = this::openActionViewIntent
                )
            }
        )
    }

    @Composable
    @Preview
    fun PreviewMainScreen() {
        MainScreen()
    }

    private fun openActionViewIntent(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    private fun openShareIntent(textToShare: String, subject: String? = null) {
        val textype = "text/plain"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare)
        sendIntent.type = textype
        subject?.let { sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject) }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
    }

}
