package com.ocram.qichwadic.features.search.data.datastore

import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.remote.CloudDataStore
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import com.ocram.qichwadic.core.data.remote.RetrofitClient

interface SearchCloudDataStore {
    suspend fun search(fromQuechua: Int, target: String, word: String, searchType: Int): ApiResponse<out List<SearchResultEntity>>

    suspend fun fetchMoreResults(dictionaryId: Int, word: String, searchType: Int, page: Int): ApiResponse<out SearchResultEntity>
}

class SearchCloudDataStoreImpl(private val retrofitClient: RetrofitClient): CloudDataStore(), SearchCloudDataStore {

    override suspend fun search(fromQuechua: Int, target: String, word: String, searchType: Int): ApiResponse<out List<SearchResultEntity>> {
        return processResponse(retrofitClient.searchWord(word, fromQuechua, target, searchType))
    }

    override suspend fun fetchMoreResults(dictionaryId: Int, word: String, searchType: Int, page: Int): ApiResponse<out SearchResultEntity> {
        return processResponse(retrofitClient.fetchMoreResults(dictionaryId, word, 0, searchType, page))
    }
}
