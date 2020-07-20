package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel

interface DictionaryRepository {

    suspend fun getCloudDictionaries(): List<DictionaryModel>

    fun getSavedDictionaries(): List<DictionaryEntity>

    suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel>

    fun saveDictionaryAndDefinitions(dictionary: DictionaryModel, definitions: List<DefinitionModel>)

    fun removeDictionary(id: Int): Int
}