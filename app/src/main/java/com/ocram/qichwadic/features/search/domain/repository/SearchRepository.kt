package com.ocram.qichwadic.features.search.domain.repository

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.domain.model.SearchResultModel

interface SearchRepository {

    suspend fun searchOnline(fromQuechua: Int, target: String, word: String, searchType: Int): List<SearchResultModel>

    suspend fun searchOffline(fromQuechua: Int, target: String, word: String, searchType: Int): List<SearchResultModel>

    suspend fun fetchMoreResultsOffline(dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionEntity>

    suspend fun fetchMoreResultsOnline(dictionaryId: Int, searchType: Int, word: String, page: Int): SearchResultModel
}