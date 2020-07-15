package com.ocram.qichwadic.features.dictionaries.data.datastore

import com.ocram.qichwadic.core.data.ApiResponse
import com.ocram.qichwadic.core.data.CloudDataStore
import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.DictionaryEntity
import com.ocram.qichwadic.features.common.data.remote.RetrofitClient

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
