package com.ocram.qichwadic.features.dictionaries.data.repository

import com.ocram.qichwadic.core.data.model.*
import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryCloudDataStore
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryLocalDataStore
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultDictionaryRepository(
    private val localDataStore: DictionaryLocalDataStore,
    private val cloudDataStore: DictionaryCloudDataStore) : DictionaryRepository {

    override suspend fun getCloudDictionaries(): List<DictionaryModel> {
        return when (val response = cloudDataStore.getDictionaries()) {
            is ApiResponse.Success -> {
                response.data.map { it.toDictionaryModel() }
            }
            else ->emptyList()
        }
    }

    override suspend fun saveNewDictionaries(dictionaries: List<DictionaryModel>) {
        localDataStore.saveDictionaries(dictionaries.map(DictionaryEntity::fromDictionaryModel))
    }

    override fun getSavedDictionaries(): Flow<List<DictionaryModel>> {
        return localDataStore.getDictionaries().map { list -> list.map { it.toDictionaryModel() } }
    }

    override suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel> {
        return when (val response = cloudDataStore.getDefinitionsByDictionary(dictionaryId)) {
            is ApiResponse.Success -> response.data.map { it.toDefinitionModel() }
            else -> emptyList()
        }
    }

    override suspend fun saveDefinitions(definitions: List<DefinitionModel>) {
        localDataStore.saveDefinitions(
            definitions.map { DefinitionEntity.fromDefinitionModel(it) }
        )
    }

    override suspend fun removeDictionary(id: Int): Int {
        return localDataStore.removeDictionary(id)
    }
}


