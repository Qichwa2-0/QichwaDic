package com.ocram.qichwadic.features.dictionaries.data.repository

import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryCloudDataStore
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryLocalDataStore
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryRepository

class DefaultDictionaryRepository
constructor(private val localDataStore: DictionaryLocalDataStore, private val cloudDataStore: DictionaryCloudDataStore) : DictionaryRepository {

    override suspend fun getCloudDictionaries(): List<DictionaryModel> {
        return when (val response = cloudDataStore.getDictionaries()) {
            is ApiResponse.Success -> response.data.map { it.toDictionaryModel() }
            else -> emptyList()
        }
    }

    override suspend fun getSavedDictionaries(): List<DictionaryEntity> {
        return localDataStore.getDictionaries()
    }

    override suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return when (val response = cloudDataStore.getDefinitionsByDictionary(dictionaryId)) {
            is ApiResponse.Success -> response.data.map {it.toDefinitionModel() }
            else -> emptyList()
        }
    }

    override suspend fun saveDictionaryAndDefinitions(dictionary: DictionaryModel, definitions: List<DefinitionModel>) {
        localDataStore.saveDictionaryAndDefinitions(
                DictionaryEntity.fromDictionaryModel(dictionary),
                definitions.map { DefinitionEntity.fromDefinitionModel(it) }
        )
    }

    override suspend fun removeDictionary(id: Int): Int {
        return localDataStore.removeDictionary(id)
    }
}


