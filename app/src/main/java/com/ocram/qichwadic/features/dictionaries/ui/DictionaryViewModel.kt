package com.ocram.qichwadic.features.dictionaries.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.*
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import kotlinx.coroutines.Dispatchers
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
    val allDictionaries: LiveData<List<DictionaryModel>> = MutableLiveData(),
    val downloadProgress: SnapshotStateMap<Int, Boolean> = mutableStateMapOf(),
    val loadingDictionaries: Boolean = true,
    val cloudLoadingError: Boolean = false,
    val selectedLanguage: String = DEFAULT_LANG_CODE,
    val dictionaryDownloadState: DictionaryDownloadState? = null
)

class DictionaryViewModel(private val interactor: DictionaryInteractor) : ViewModel() {

    var state by mutableStateOf(DictionaryUiState())
        private set

    init {
        loadDictionaries()
    }

    private fun loadDictionaries() {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.refreshCloudDictionaries()
        }
        viewModelScope.launch {
            val liveData = interactor
                .getDictionaries()
                .catch { setState { copy(cloudLoadingError = true) } }
                .asLiveData()
                .map { dictionaries ->
                    val downloadProgress = dictionaries.map { it.id to false }
                    setState {
                        copy(
                            loadingDictionaries = false,
                            downloadProgress = downloadProgress.toMutableStateMap()
                        )
                    }
                    dictionaries
                }
            setState { copy(allDictionaries = liveData) }
        }
    }

    fun onLanguageSelected(lang: String) = setState { copy(selectedLanguage = lang) }

    fun onDownloadClicked(dictionary: DictionaryModel) {
        state.downloadProgress[dictionary.id] = true
        viewModelScope.launch {
            try {
                val actionStatus = if(dictionary.existsInLocal) {
                    removeDictionary(dictionary)
                } else {
                    downloadDictionary(dictionary)
                }
                setState { copy(dictionaryDownloadState = actionStatus) }
            } finally {
                state.downloadProgress[dictionary.id] = false
            }
        }
    }

    private suspend fun downloadDictionary(dictionary: DictionaryModel): DictionaryDownloadState {
        val name = dictionary.name ?: ""
        return try {
            interactor.saveDefinitions(dictionary)
            SaveSuccess(name)
        } catch (e : Throwable) {
            SaveError(name)
        }
    }

    private suspend fun removeDictionary(dictionary: DictionaryModel): DictionaryDownloadState {
        val removedTotal = interactor.removeDictionary(dictionary.id)
        val name = dictionary.name ?: ""
        return if (removedTotal) DeleteSuccess(name) else DeleteError(name)
    }

    private fun setState(applyState: DictionaryUiState.() -> DictionaryUiState) {
        state = state.applyState()
    }
}
