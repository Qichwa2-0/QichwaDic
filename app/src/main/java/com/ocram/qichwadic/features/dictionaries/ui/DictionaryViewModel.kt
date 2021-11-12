package com.ocram.qichwadic.features.dictionaries.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class DictionaryDownloadState(@StringRes val strResourceId: Int, val dictionaryName: String)
class SaveError(dictionaryName: String)
    : DictionaryDownloadState(R.string.dictionary_save_error, dictionaryName)
class SaveSuccess(dictionaryName: String)
    : DictionaryDownloadState(R.string.dictionary_save_success, dictionaryName)
class DeleteError(dictionaryName: String)
    : DictionaryDownloadState(R.string.dictionary_delete_error, dictionaryName)
class DeleteSuccess(dictionaryName: String)
    : DictionaryDownloadState(R.string.dictionary_delete_success, dictionaryName)

const val DEFAULT_LANG_CODE = "qu"

data class DictionaryUiState(
    var loadingDictionaries: Boolean = true,
    var cloudLoadingError: Boolean = false,
    var selectedLanguage: String = DEFAULT_LANG_CODE,
    var dictionaryDownloadState: DictionaryDownloadState? = null,
)

class DictionaryViewModel(private val interactor: DictionaryInteractor) : ViewModel() {

    var dictionaryUiState by mutableStateOf(DictionaryUiState())
        private set

    var allDictionaries = mutableStateListOf<DictionaryModel>()
        private set

    init {
        loadDictionaries()
    }

    private fun loadDictionaries() {
        viewModelScope.launch {
            interactor.refreshCloudDictionaries()
            interactor
                .getDictionaries()
                .catch { dictionaryUiState = dictionaryUiState.copy(cloudLoadingError = true) }
                .collect {
                    dictionaryUiState = dictionaryUiState.copy(loadingDictionaries  = false)
                    allDictionaries.apply {
                        clear()
                        addAll(it)
                    }
                }
        }
    }

    fun onLanguageSelected(lang: String) {
        dictionaryUiState = dictionaryUiState.copy(selectedLanguage = lang)
    }

    fun onDownloadClicked(dictionary: DictionaryModel) {
        val index = allDictionaries.indexOf(dictionary)
        allDictionaries[index] = dictionary.copy(downloading = true)
        viewModelScope.launch {
            try {
                val actionStatus = if(dictionary.existsInLocal) {
                    removeDictionary(dictionary)
                } else {
                    downloadDictionary(dictionary)
                }
                dictionaryUiState = dictionaryUiState.copy(dictionaryDownloadState = actionStatus)
            } finally {
                allDictionaries[index] = dictionary.copy(downloading = false)
            }
        }
    }

    private suspend fun downloadDictionary(dictionary: DictionaryModel): DictionaryDownloadState {
        val name = dictionary.name ?: ""// _allDictionaries[pos].name ?: ""
        return try {
            interactor.saveDefinitions(dictionary)
            SaveSuccess(name)
        } catch (e : Throwable) {
            SaveError(name)
        }
    }

    private suspend fun removeDictionary(dictionary: DictionaryModel)
            : DictionaryDownloadState {
        val removedTotal = interactor.removeDictionary(dictionary.id)
        val name = dictionary.name ?: ""
        return if (removedTotal) DeleteSuccess(name) else DeleteError(name)
    }
}
