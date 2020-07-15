package com.ocram.qichwadic.features.common.data.remote

import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.DictionaryEntity
import com.ocram.qichwadic.features.common.data.model.SearchResultEntity
import retrofit2.Response

class RetrofitClient(private val retrofitService: RetrofitService) : Service {

    override suspend fun getDictionaries(): Response<List<DictionaryEntity>> {
        return this.retrofitService.getDictionaries(1)
    }

    override suspend fun searchWord(searchWord: String, fromQuechua: Int, target: String, searchType: Int): Response<List<SearchResultEntity>> {
        return retrofitService.searchWord(1, searchWord, fromQuechua, target, searchType)
    }

    override suspend fun fetchMoreResults(dictionaryId: Int, word: String, fromQuechua: Int, searchType: Int, page: Int): Response<SearchResultEntity> {
        return this.retrofitService.fetchMoreResults(dictionaryId, word, fromQuechua, searchType, page)
    }

    override suspend fun getAllDefinitionsByDictionary(dictionaryId: Int): Response<List<DefinitionEntity>> {
        return this.retrofitService.getAllDefinitionsByDictionary(dictionaryId)
    }
}
