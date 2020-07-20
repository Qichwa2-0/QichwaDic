package com.ocram.qichwadic.features.dictionaries.domain

import android.util.Log
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

interface DictionaryInteractor {

    fun getSavedDictionaries(): List<DictionaryModel>

    suspend fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel)

    suspend fun removeDictionary(id: Int): Boolean

    suspend fun getAllDictionaries(): DictionaryFetchResult
}


class DictionaryInteractorImpl(private val dictionaryRepository: DictionaryRepository) : DictionaryInteractor {

    override suspend fun getAllDictionaries(): DictionaryFetchResult {

        val fetchResult = DictionaryFetchResult()
        withContext(Dispatchers.IO) {
            fetchResult.localDictionaries = getSavedDictionaries()
            try {
                fetchResult.cloudDictionaries = dictionaryRepository.getCloudDictionaries()
            } catch (_: Exception) {
                Log.e("ERROR", "Error loading cloud dictionaries")
                fetchResult.addError()
            }

            fetchResult.mapDictionaries()
        }
        return fetchResult
    }

    override fun getSavedDictionaries(): List<DictionaryModel> {
        return dictionaryRepository.getSavedDictionaries().map { it.toDictionaryModel() }
    }

    override suspend fun saveDictionaryAndDefinitions(dictionaryModel: DictionaryModel) {
        withContext(Dispatchers.IO) {
            val definitions = getAllDefinitionsByDictionary(dictionaryModel.id)
            definitions.forEach {
                it.dictionaryId = dictionaryModel.id
                it.dictionaryName = dictionaryModel.name
            }
            dictionaryRepository.saveDictionaryAndDefinitions(dictionaryModel, definitions)
        }
    }


    private suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return dictionaryRepository.getDefinitionsByDictionary(dictionaryId)
    }

    override suspend fun removeDictionary(id: Int): Boolean {
        var removed = false
        withContext(Dispatchers.IO) {
            removed = dictionaryRepository.removeDictionary(id) > 0
        }
        return removed
    }
}
