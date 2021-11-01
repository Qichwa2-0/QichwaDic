package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel

interface DictionaryRepository {

    suspend fun getCloudDictionaries(): List<DictionaryModel>

    suspend fun getSavedDictionaries(): List<DictionaryEntity>

    suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel>

    suspend fun saveDictionaryAndDefinitions(dictionary: DictionaryModel, definitions: List<DefinitionModel>)

    suspend fun removeDictionary(id: Int): Int
}