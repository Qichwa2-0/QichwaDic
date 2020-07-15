package com.ocram.qichwadic.features.dictionaries.data.datastore

import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.DictionaryEntity
import com.ocram.qichwadic.features.common.data.local.dao.DictionaryDao

interface DictionaryLocalDataStore {

    fun getDictionaries(): List<DictionaryEntity>

    fun saveDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>)

    fun removeDictionary(id: Int): Int
}

class DictionaryLocalDataStoreImpl
constructor(private val dictionaryDao: DictionaryDao): DictionaryLocalDataStore {

    override fun getDictionaries(): List<DictionaryEntity> {
        return dictionaryDao.getDictionaries()
    }

    override fun saveDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>) {
        return dictionaryDao.insertDictionaryAndDefinitions(dictionary, definitions)
    }

    override fun removeDictionary(id: Int): Int {
        return dictionaryDao.removeOne(id)
    }

}
