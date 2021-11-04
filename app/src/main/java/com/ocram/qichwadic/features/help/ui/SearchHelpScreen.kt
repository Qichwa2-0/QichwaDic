package com.ocram.qichwadic.features.help.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
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
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.TopBar

@Composable
fun SearchHelpScreen(
    onBackPressed: () -> Unit,
    openActionWebview: (uri: String) -> Unit
) {
    val context = LocalContext.current

    var tabState by remember { mutableStateOf(0) }
    val tabTitles = remember { listOf(R.string.search_help_writing, R.string.search_help_faq) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = stringResource(id = R.string.nav_search_tips),
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
        Surface(Modifier.padding(horizontal = 16.dp)) {
            if (tabState == 0) {
                SearchHelpWritingView(openActionWebview)
            } else {
                SearchHelpFaqView()
            }
        }

    }
}

@Composable
@Preview
fun PreviewSearchHelpScreen() {
    SearchHelpScreen({}, {})
}