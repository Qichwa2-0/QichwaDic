package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {

    suspend fun getCloudDictionaries(): List<DictionaryModel>

    suspend fun saveNewDictionaries(dictionaries: List<DictionaryModel>)

    suspend fun getSavedDictionaries(): Flow<List<DictionaryModel>>

    suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel>

    suspend fun saveDefinitions(definitions: List<DefinitionModel>)

    suspend fun removeDictionary(id: Int): Int
}