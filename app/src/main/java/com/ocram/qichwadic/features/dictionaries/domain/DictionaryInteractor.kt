package com.ocram.qichwadic.features.dictionaries.domain

import android.util.Log
import com.ocram.qichwadic.features.common.domain.DefinitionModel
import com.ocram.qichwadic.features.common.domain.DictionaryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

interface DictionaryInteractor {

    fun getSavedDictionaries(): List<DictionaryModel>

    suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel>

    fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel, definitions: List<DefinitionModel>)

    fun removeDictionary(id: Int): Boolean

    suspend fun getAllDictionaries(): Map<String, List<DictionaryModel>>
}


class DictionaryInteractorImpl(private val dictionaryRepository: DictionaryRepository) : DictionaryInteractor {

    override suspend fun getAllDictionaries(): Map<String, List<DictionaryModel>> {

        val dictionariesByLang =  mutableMapOf<String, MutableList<DictionaryModel>>()

        withContext(Dispatchers.IO) {
            val savedDictionaries = getSavedDictionaries()

            val allDictionaries = mutableSetOf<DictionaryModel>()
            try {
                allDictionaries.addAll(dictionaryRepository.getDictionaries())
            } catch (_: Exception) {
                Log.e("ERROR", "Error loading cloud dictionaries")
            }

            savedDictionaries.forEach { savedDictionary ->
                val dictionary =
                        allDictionaries.find { cloudDictionary -> cloudDictionary == savedDictionary }
                                ?: savedDictionary
                dictionary.existsInLocal = true
                allDictionaries.add(savedDictionary)
            }


            val sortedDictionaries = allDictionaries.toMutableList().sortedDescending()//.sortedWith(Comparator())

            sortedDictionaries.forEach { dictionary ->
                dictionary.languageBegin?.let { languageBegin ->
                    val dictionaryModels: MutableList<DictionaryModel> = dictionariesByLang[languageBegin] ?: mutableListOf()
                    dictionaryModels.add(dictionary)
                    dictionariesByLang.put(languageBegin, dictionaryModels)
                }
            }
        }
        return dictionariesByLang
    }

    override fun getSavedDictionaries(): List<DictionaryModel> {
        return dictionaryRepository.getSavedDictionaries().map { it.toDictionaryModel() }
    }

    override suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return dictionaryRepository.getDefinitionsByDictionary(dictionaryId)
    }

    override fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel, definitions: List<DefinitionModel>) {
        definitions.forEach {
            it.dictionaryId = dictionaryModel.id
            it.dictionaryName = dictionaryModel.name
        }
        dictionaryRepository.saveDictionaryAndDefinitions(dictionaryModel, definitions)
    }

    override fun removeDictionary(id: Int): Boolean {
        return dictionaryRepository.removeDictionary(id) > 0
    }
}
