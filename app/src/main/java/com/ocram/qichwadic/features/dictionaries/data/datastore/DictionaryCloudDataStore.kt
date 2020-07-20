package com.ocram.qichwadic.features.dictionaries.data.datastore

import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.remote.CloudDataStore
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.remote.RetrofitClient

interface DictionaryCloudDataStore {

    suspend fun getDictionaries(): ApiResponse<out List<DictionaryEntity>>

    suspend fun getDefinitionsByDictionary(dictionaryId: Int): ApiResponse<out List<DefinitionEntity>>
}

class DictionaryCloudDataStoreImpl
constructor(private val retrofitClient: RetrofitClient): CloudDataStore(), DictionaryCloudDataStore {

    override suspend fun getDictionaries(): ApiResponse<out List<DictionaryEntity>> {
        return processResponse(retrofitClient.getDictionaries())
    }

    override suspend fun getDefinitionsByDictionary(dictionaryId: Int): ApiResponse<out List<DefinitionEntity>> {
        return processResponse(retrofitClient.getAllDefinitionsByDictionary(dictionaryId))
    }
}
