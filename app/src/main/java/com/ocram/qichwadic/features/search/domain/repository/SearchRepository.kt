package com.ocram.qichwadic.features.search.domain.repository

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams

interface SearchRepository {

    suspend fun search(searchParams: SearchParams): List<SearchResultModel>

    suspend fun fetchMoreResults(
        dictionaryId: Int,
        searchType: Int,
        word: String,
        page: Int
    ): List<DefinitionModel>
}