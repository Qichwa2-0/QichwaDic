package com.ocram.qichwadic.features.dictionaries.domain

import android.util.Log
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import java.lang.Exception

interface DictionaryInteractor {

    suspend fun getSavedDictionaries(): List<DictionaryModel>

    suspend fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel)

    suspend fun removeDictionary(id: Int): Boolean

    suspend fun getAllDictionaries(): DictionaryFetchResult
}

data class DictionaryFetchResult(
    var cloudError: Boolean = false,
    var allDictionaries: List<DictionaryModel> = emptyList()
)

class DictionaryInteractorImpl(private val dictionaryRepository: DictionaryRepository) : DictionaryInteractor {

    override suspend fun getAllDictionaries(): DictionaryFetchResult {
        val result = DictionaryFetchResult()
        try {
            result.allDictionaries = getSavedDictionaries()
            result.allDictionaries =
                result.allDictionaries.union(dictionaryRepository.getCloudDictionaries()).sorted()
        } catch (_: Exception) {
            Log.e("ERROR", "Error loading cloud dictionaries")
            result.cloudError = true
        }
        return result
    }

    override suspend fun getSavedDictionaries(): List<DictionaryModel> {
        return dictionaryRepository.getSavedDictionaries().map {
            it.toDictionaryModel().apply { existsInLocal = true }
        }
    }

    override suspend fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel) {
        val definitions = getAllDefinitionsByDictionary(dictionaryModel.id)
        definitions.forEach {
            it.dictionaryId = dictionaryModel.id
            it.dictionaryName = dictionaryModel.name
        }
        dictionaryRepository.saveDictionaryAndDefinitions(dictionaryModel, definitions)
    }


    private suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return dictionaryRepository.getDefinitionsByDictionary(dictionaryId)
    }

    override suspend fun removeDictionary(id: Int): Boolean {
        return dictionaryRepository.removeDictionary(id) > 0
    }
}
