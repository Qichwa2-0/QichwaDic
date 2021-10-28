package com.ocram.qichwadic.features.dictionaries.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.ui.common.TopBar

@Composable
fun DictionaryScreen(
    uiState: DictionaryUiState,
    dictionaries: List<DictionaryModel>,
    showSnackbar: (message: String) -> Unit,
    onBackPressed: () -> Unit,
    onDictionaryLanguageSelected: (lang: String) -> Unit,
    onDownloadClicked: (DictionaryModel) -> Unit
) {
    println(uiState.loadingDictionaries)
    if(uiState.cloudLoadingError) {
        val onErrorText = stringResource(id = R.string.error_no_cloud_dictionaries)
        LaunchedEffect(uiState.cloudLoadingError) {
            showSnackbar(onErrorText)
        }
    }
    uiState.dictionaryDownloadState?.let {
        val message = stringResource(it.strResourceId, it.dictionaryName)
        LaunchedEffect(it) {
            showSnackbar(message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Dictionaries",
            buttonIcon = Icons.Filled.ArrowBack,
            onButtonClicked = { onBackPressed() }
        )
        if(uiState.loadingDictionaries) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                DictionaryDropDown(
                    items = uiState.dictionaryLanguages,
                    selectedLanguage = uiState.selectedLanguage,
                    onItemSelected = onDictionaryLanguageSelected
                )
                LazyColumn {
                    items(
                        items = dictionaries
                            .filter { it.languageBegin == uiState.selectedLanguage }
                            .sorted(),
                        key = { dictionary -> dictionary.id }
                    ) {
                        DictionaryCard(dictionary = it) { onDownloadClicked(it) }
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun DictionaryScreenPreview() {
    DictionaryScreen(
        DictionaryUiState(),
        listOf(),
        {},
        onBackPressed = {},
        onDictionaryLanguageSelected = {}
    ) {}
}

@Composable
@Preview
fun DictionaryCardPreview() {
    DictionaryCard(
        dictionary = DictionaryModel(
            id = 1,
            name = "Dictionary",
            description = "some description",
            author = "the author",
            totalEntries = 199
        )
    ) {}
}