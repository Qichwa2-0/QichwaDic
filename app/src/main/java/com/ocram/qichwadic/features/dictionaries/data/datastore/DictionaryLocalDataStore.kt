package com.ocram.qichwadic.features.dictionaries.data.datastore

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.local.dao.DictionaryDao
import com.ocram.qichwadic.core.data.model.DictionaryWithStatusEntity
import kotlinx.coroutines.flow.Flow

interface DictionaryLocalDataStore {

    fun getDictionaries(): Flow<List<DictionaryWithStatusEntity>>

    suspend fun saveDictionaries(dictionaries: List<DictionaryEntity>)

    suspend fun saveDefinitions(definitions: List<DefinitionEntity>)

    suspend fun removeDictionary(id: Int): Int
}

class DictionaryLocalDataStoreImpl(private val dictionaryDao: DictionaryDao): DictionaryLocalDataStore {

    override fun getDictionaries(): Flow<List<DictionaryWithStatusEntity>> {
        return dictionaryDao.getDictionariesWithStatus()
    }

    override suspend fun saveDictionaries(dictionaries: List<DictionaryEntity>) {
        return dictionaryDao.insertDictionaries(dictionaries)
    }

    override suspend fun saveDefinitions(definitions: List<DefinitionEntity>) {
        return dictionaryDao.insertDefinitions( definitions)
    }

    override suspend fun removeDictionary(id: Int): Int {
        return dictionaryDao.removeDefinitions(id)
    }

}
