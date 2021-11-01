package com.ocram.qichwadic.features.search.domain.repository

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams

interface SearchRepository {

    suspend fun searchOnline(searchParams: SearchParams): List<SearchResultModel>

    suspend fun searchOffline(searchParams: SearchParams): List<SearchResultModel>

    suspend fun fetchMoreResultsOffline(dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionEntity>

    suspend fun fetchMoreResultsOnline(dictionaryId: Int, searchType: Int, word: String, page: Int): SearchResultModel
}