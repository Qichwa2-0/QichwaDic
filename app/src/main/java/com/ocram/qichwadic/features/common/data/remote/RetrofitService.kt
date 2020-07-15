package com.ocram.qichwadic.features.common.data.remote

import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.DictionaryEntity
import com.ocram.qichwadic.features.common.data.model.SearchResultEntity
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET(ALL_DICTIONARIES)
    suspend fun getDictionaries(@Query("all") all: Int): Response<List<DictionaryEntity>>

    @GET(SEARCH_WORDS)
    suspend fun searchWord(
            @Query("all") all: Int,
            @Query("searchWord") searchWord: String,
            @Query("fromQuechua") fromQuechua: Int,
            @Query("target") target: String,
            @Query("searchType") searchType: Int
    ): Response<List<SearchResultEntity>>

    @GET("$ALL_DICTIONARIES/{dictionaryId}/words/fetch")
    suspend fun fetchMoreResults(
            @Path("dictionaryId") dictionaryId: Int,
            @Query("searchWord") searchWord: String,
            @Query("fromQuechua") fromQuechua: Int,
            @Query("searchType") searchType: Int,
            @Query("page") page: Int
    ): Response<SearchResultEntity>

    @GET("$ALL_DICTIONARIES/{dictionaryId}/words")
    suspend fun getAllDefinitionsByDictionary(@Path("dictionaryId") dictionaryId: Int): Response<List<DefinitionEntity>>

    companion object {

        const val API_URL = BuildConfig.SERVER_URL
        const val ALL_DICTIONARIES = API_URL + "dictionaries"
        const val SEARCH_WORDS = API_URL + "words"
    }
}
