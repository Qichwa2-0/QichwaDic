package com.ocram.qichwadic.features.dictionaries.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.ui.common.LinearLoadingIndicator
import com.ocram.qichwadic.core.ui.view.DictLang
import com.ocram.qichwadic.core.ui.common.TopBar
import org.koin.androidx.compose.getViewModel

@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel = getViewModel(),
    showSnackbar: (message: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState = viewModel.state
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
    val dictionaries by uiState.allDictionaries.observeAsState(emptyList())
    DictionariesView(
        loadingDictionaries = uiState.loadingDictionaries,
        selectedLanguage = uiState.selectedLanguage,
        dictionaries = dictionaries,
        dictionariesProgress = uiState.downloadProgress,
        onBackPressed = onBackPressed,
        onDictionaryLanguageSelected = viewModel::onLanguageSelected,
        onDownloadClicked = viewModel::onDownloadClicked
    )
}

@Composable
fun DictionariesView(
    loadingDictionaries: Boolean,
    selectedLanguage: String,
    dictionaries: List<DictionaryModel>,
    dictionariesProgress: Map<Int, Boolean>,
    onBackPressed: () -> Unit,
    onDictionaryLanguageSelected: (language: String) -> Unit,
    onDownloadClicked: (DictionaryModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = stringResource(id = R.string.nav_dictionaries),
            buttonIcon = Icons.Filled.ArrowBack,
            onButtonClicked = onBackPressed
        )
        Surface(Modifier.fillMaxSize()) {
            if(loadingDictionaries) {
                LinearLoadingIndicator(loadingMessageId = R.string.loading_dictionaries)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    DictionaryDropDown(
                        items = stringArrayResource(id = R.array.dictLangs).map { DictLang(it) },
                        selectedLanguage = selectedLanguage,
                        onItemSelected = onDictionaryLanguageSelected
                    )
                    LazyColumn {
                        items(
                            items = dictionaries.filter { it.languageBegin == selectedLanguage },
                            key = { dictionary -> dictionary.id }
                        ) { item ->
                            DictionaryCard(
                                dictionary = item,
                                downloading = dictionariesProgress[item.id] ?: false
                            ) { onDownloadClicked(item) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DictionariesViewPreview() {
    DictionariesView(
        loadingDictionaries = false,
        selectedLanguage = "qu",
        dictionaries = listOf(
            DictionaryModel(1, "Dictionary 1", languageBegin = "qu"),
            DictionaryModel(2, "Dictionary 2", languageBegin = "qu")
        ),
        dictionariesProgress = mapOf(1 to false, 2 to true),
        onBackPressed = {},
        onDictionaryLanguageSelected = {}
    ) {}
}