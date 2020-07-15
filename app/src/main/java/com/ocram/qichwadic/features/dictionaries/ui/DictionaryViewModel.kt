package com.ocram.qichwadic.features.dictionaries.ui

import androidx.lifecycle.*
import com.ocram.qichwadic.features.common.domain.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class DictionaryViewModel(private val interactor: DictionaryInteractor) : ViewModel() {

    var dictionariesByLang: MutableLiveData<out Map<String, List<DictionaryModel>>> = MutableLiveData()
    var dictionaryActionStatus = MutableLiveData<DictionaryActionState>()
    var localLoading = MutableLiveData<Boolean>(true)

    init {
        loadDictionaries()
    }

    private fun loadDictionaries() {
        viewModelScope.launch {
            localLoading.value = true
            try {
                dictionariesByLang.value = interactor.getAllDictionaries()
            } catch (_: Exception) {
                dictionariesByLang.value = emptyMap()
            } finally {
                localLoading.value = false
            }
        }
    }

    fun downloadDictionary(pos: Int, dictionary: DictionaryModel) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val definitions = interactor.getAllDefinitionsByDictionary(dictionary.id)
                    interactor.saveDictionaryAndDefinitions(dictionary, definitions)
                }
                dictionary.existsInLocal = true
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary))
            } catch (e : Throwable) {
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary, true))
            }
        }
    }

    fun removeDictionary(pos: Int, dictionary: DictionaryModel) {
        viewModelScope.launch {
             withContext(Dispatchers.IO) {
                val removedTotal = interactor.removeDictionary(dictionary.id)
                if (removedTotal) {
                    dictionary.existsInLocal = false
                    dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary))
                } else {
                    dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary, true))
                }
            }
        }
    }

}
