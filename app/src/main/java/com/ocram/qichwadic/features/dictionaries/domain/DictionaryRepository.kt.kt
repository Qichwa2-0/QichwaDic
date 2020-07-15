package com.ocram.qichwadic.features.dictionaries.domain

import com.ocram.qichwadic.features.common.data.model.DictionaryEntity
import com.ocram.qichwadic.features.common.domain.DefinitionModel
import com.ocram.qichwadic.features.common.domain.DictionaryModel

interface DictionaryRepository {

    suspend fun getDictionaries(): List<DictionaryModel>

    fun getSavedDictionaries(): List<DictionaryEntity>

    suspend fun getDefinitionsByDictionary(dictionaryId: Int): List<DefinitionModel>

    fun saveDictionaryAndDefinitions(dictionary: DictionaryModel, definitions: List<DefinitionModel>)

    fun removeDictionary(id: Int): Int
}