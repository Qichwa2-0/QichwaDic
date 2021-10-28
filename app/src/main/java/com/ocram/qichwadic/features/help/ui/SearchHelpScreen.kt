package com.ocram.qichwadic.features.help.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.TopBar

@Composable
fun SearchHelpScreen(
    onBackPressed: () -> Unit,
    openActionWebview: (uri: String) -> Unit
) {
    var tabState by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val tabTitles = listOf(R.string.search_help_writing, R.string.search_help_faq)
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Ayuda",
            buttonIcon = Icons.Filled.ArrowBack,
            onButtonClicked = { onBackPressed() }
        )
        TabRow(selectedTabIndex = tabState){
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(context.getString(title)) },
                    selected = tabState == index,
                    onClick = { tabState = index }
                )
            }
        }
        if (tabState == 0) {
            SearchHelpWritingView(openActionWebview)
        } else {
            SearchHelpFaqView()
        }
    }
}

@Composable
@Preview
fun PreviewSearchHelpScreen() {
    SearchHelpScreen({}, {})
}