package com.ocram.qichwadic.features.dictionaries.data.datastore

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.local.dao.DictionaryDao

interface DictionaryLocalDataStore {

    suspend fun getDictionaries(): List<DictionaryEntity>

    suspend fun saveDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>)

    suspend fun removeDictionary(id: Int): Int
}

class DictionaryLocalDataStoreImpl
constructor(private val dictionaryDao: DictionaryDao): DictionaryLocalDataStore {

    override suspend fun getDictionaries(): List<DictionaryEntity> {
        return dictionaryDao.getDictionaries()
    }

    override suspend fun saveDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>) {
        return dictionaryDao.insertDictionaryAndDefinitions(dictionary, definitions)
    }

    override suspend fun removeDictionary(id: Int): Int {
        return dictionaryDao.removeOne(id)
    }

}
