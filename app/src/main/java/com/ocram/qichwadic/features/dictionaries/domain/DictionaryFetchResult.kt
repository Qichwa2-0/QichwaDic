package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.core.domain.model.DictionaryModel

class DictionaryFetchResult {

    var cloudDictionaries: List<DictionaryModel> = emptyList()
    var cloudError = false
    var localDictionaries: List<DictionaryModel> = emptyList()
    var dictionariesByLang = mutableMapOf<String, MutableList<DictionaryModel>>()

    fun addError() {
        cloudError = true
        cloudDictionaries = emptyList()
    }

    internal fun mapDictionaries() {
        mergeDictionaries()
            .sorted()
            .forEach { dictionary ->
                dictionary.languageBegin?.let {
                    val dictionaryModels = dictionariesByLang[it] ?: mutableListOf()
                    dictionaryModels.add(dictionary)
                    dictionariesByLang[it] = dictionaryModels
                }
            }
    }

    private fun mergeDictionaries(): Set<DictionaryModel> {
        val allDictionaries = mutableSetOf<DictionaryModel>()
        allDictionaries.addAll(cloudDictionaries)

        localDictionaries.forEach { savedDictionary ->
            val dictionary =
                    allDictionaries.find { cloudDictionary -> cloudDictionary == savedDictionary }
                            ?: savedDictionary
            dictionary.existsInLocal = true
            allDictionaries.add(savedDictionary)
        }
        return allDictionaries
    }
}