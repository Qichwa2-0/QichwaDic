package com.ocram.qichwadic.features.help.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.theme.listItemHeaderColor

@Composable
fun SearchHelpWritingView(openActionWebview: (uri: String) -> Unit) {
    val openYtUrl = {
        val ytUrl = "https://www.youtube.com/watch?v=wgCq5X3ezKo&list=PLYJ5xaOE7Z-kmctE2OICkvVH_GFDEj_zP"
        openActionWebview(ytUrl)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.search_help_writing_tips_intro),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = stringResource(R.string.search_help_writing_tips),
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.search_help_writing_video_text),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = listItemHeaderColor
        )
        Image(
            modifier = Modifier.padding(vertical = 16.dp).clickable { openYtUrl() },
            painter = painterResource(R.drawable.qillqay_youtube_video),
            contentDescription = ""
        )
        Button(onClick = openYtUrl) {
            Text(text = "Ver")
        }
    }
}

@Composable
fun SearchHelpFaqView() {
    val textParts = mapOf(
        R.string.search_help_faq_translator to R.string.search_help_faq_translator_reply,
        R.string.search_help_faq_no_results to R.string.search_help_faq_no_results_reply,
        R.string.search_help_faq_no_results_complex_word to R.string.search_help_faq_no_results_complex_word_reply
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())) {
        textParts.map { ContentWithTitle(it.key, it.value) }
    }
}

@Composable
fun ContentWithTitle(@StringRes titleResId: Int, @StringRes contentResId: Int) {
    Text(text = stringResource(titleResId), fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(text = stringResource(contentResId), fontSize = 14.sp)
}

@Composable
@Preview
fun PreviewSearchHelpWritingView() {
    SearchHelpWritingView {}
}

@Composable
@Preview
fun PreviewSearchHelpFaqView() {
    SearchHelpFaqView()
}