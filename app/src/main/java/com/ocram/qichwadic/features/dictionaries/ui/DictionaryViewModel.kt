package com.ocram.qichwadic.features.dictionaries.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import kotlinx.coroutines.launch
import java.lang.Exception

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
    var dictionaryDownloadState: DictionaryDownloadState? = null
)

class DictionaryViewModel(private val interactor: DictionaryInteractor) : ViewModel() {

    var dictionaryUiState by mutableStateOf(DictionaryUiState())
        private set

    val allDictionaries = mutableStateListOf<DictionaryModel>()

    init { loadDictionaries() }

    private fun loadDictionaries() {
        viewModelScope.launch {
            try {
                val fetchResult = interactor.getAllDictionaries()
                allDictionaries.apply {
                    clear()
                    addAll(fetchResult.allDictionaries)
                }
                dictionaryUiState = dictionaryUiState.copy(cloudLoadingError = fetchResult.cloudError)
            } catch (_: Exception) {
                allDictionaries.clear()
            } finally {
                dictionaryUiState = dictionaryUiState.copy(loadingDictionaries = false)
            }
        }
    }

    fun onLanguageSelected(lang: String) {
        dictionaryUiState = dictionaryUiState.copy(selectedLanguage = lang)
    }

    fun onDownloadClicked(dictionary: DictionaryModel) {
        viewModelScope.launch {
            val pos = allDictionaries.indexOf(dictionary)
            allDictionaries[pos] = dictionary.copy(downloading = true)
            val actionStatus = if(dictionary.existsInLocal) {
                removeDictionary(pos, dictionary)
            } else {
                downloadDictionary(pos, dictionary)
            }
            dictionaryUiState = dictionaryUiState.copy(dictionaryDownloadState = actionStatus)
        }
    }

    private suspend fun downloadDictionary(pos: Int, dictionary: DictionaryModel)
    : DictionaryDownloadState {
        val name = allDictionaries[pos].name ?: ""
        return try {
            interactor.saveDictionaryAndDefinitions(dictionary)
            allDictionaries[pos] = dictionary.copy(downloading = false, existsInLocal = true)
            SaveSuccess(name)
        } catch (e : Throwable) {
            SaveError(name)
        }
    }

    private suspend fun removeDictionary(pos: Int, dictionary: DictionaryModel)
    : DictionaryDownloadState {
        val removedTotal = interactor.removeDictionary(dictionary.id)
        if (removedTotal) {
            allDictionaries[pos] = dictionary.copy(downloading = false, existsInLocal = false)
        }
        val name = allDictionaries[pos].name ?: ""
        return if (removedTotal) DeleteSuccess(name) else DeleteError(name)
    }
}
