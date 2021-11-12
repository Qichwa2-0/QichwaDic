package com.ocram.qichwadic.features.search.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository

interface SearchInteractor {

    suspend fun queryWord(searchParams: SearchParams): List<SearchResultModel>
    suspend fun fetchMoreResults(
        offline: Boolean,
        dictionaryId: Int,
        searchType: Int,
        word: String,
        page: Int
    ): List<DefinitionModel>
}

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {

    override suspend fun queryWord(searchParams: SearchParams): List<SearchResultModel> {
        return searchRepository.search(searchParams).sortedByDescending { it.total }
    }

    override suspend fun fetchMoreResults(offline: Boolean, dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionModel> {
        return searchRepository.fetchMoreResults(dictionaryId, searchType, word, page)
    }
}
