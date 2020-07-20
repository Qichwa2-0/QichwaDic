package com.ocram.qichwadic.features.dictionaries.ui

import androidx.lifecycle.*
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import kotlinx.coroutines.launch
import java.lang.Exception

class DictionaryViewModel(private val interactor: DictionaryInteractor) : ViewModel() {

    var dictionariesByLang: MutableLiveData<out MutableMap<String, MutableList<DictionaryModel>>> = MutableLiveData()
    var dictionaryActionStatus = MutableLiveData<DictionaryActionState>()
    var localLoading = MutableLiveData<Boolean>(true)
    var cloudError = MutableLiveData<Boolean>(false)

    init {
        loadDictionaries()
    }

    private fun loadDictionaries() {
        viewModelScope.launch {
            localLoading.value = true
            cloudError.value = false
            try {
                val fetchResult = interactor.getAllDictionaries()
                dictionariesByLang.value = fetchResult.dictionariesByLang
                cloudError.value = fetchResult.cloudError
            } catch (_: Exception) {
                dictionariesByLang.value = mutableMapOf()
            } finally {
                localLoading.value = false
            }
        }
    }

    fun downloadDictionary(pos: Int, dictionary: DictionaryModel) {
        viewModelScope.launch {
            try {
                interactor.saveDictionaryAndDefinitions(dictionary)
                dictionary.existsInLocal = true
                dictionary.downloading = false
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary))
                dictionary.languageBegin?.let { reorderDictionaries(it) }
            } catch (e : Throwable) {
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary, true))
            }
        }
    }

    fun removeDictionary(pos: Int, dictionary: DictionaryModel) {
        viewModelScope.launch {
            val removedTotal = interactor.removeDictionary(dictionary.id)
            if (removedTotal) {
                dictionary.existsInLocal = false
                dictionary.downloading = false
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary))
                dictionary.languageBegin?.let { reorderDictionaries(it) }
            } else {
                dictionaryActionStatus.postValue(DictionaryActionState(pos, dictionary, true))
            }

        }
    }


    private fun reorderDictionaries(langName: String) {
        dictionariesByLang.value?.let { dictLangMap ->
            dictLangMap[langName]?.let {
                dictLangMap[langName] = it.sorted().toMutableList()
                dictionariesByLang.value = dictLangMap
            }
        }
    }
}
