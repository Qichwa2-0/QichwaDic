package com.ocram.qichwadic.features.search.domain

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository

interface SearchInteractor {

    suspend fun queryWord(offline: Boolean, searchParams: SearchParams): List<SearchResultModel>

    suspend fun queryWordOffline(searchParams: SearchParams): List<SearchResultModel>

    suspend fun queryWordOnline(searchParams: SearchParams): List<SearchResultModel>

    suspend fun fetchMoreResults(offline: Boolean, dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionModel>

}

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {

    override suspend fun queryWord(offline: Boolean, searchParams: SearchParams): List<SearchResultModel> {
        if(offline) {
            return this.queryWordOffline(searchParams)
        }
        return queryWordOnline(searchParams)
    }

    override suspend fun queryWordOffline(searchParams: SearchParams): List<SearchResultModel> {
        return searchRepository.searchOffline(searchParams)
    }

    override suspend fun queryWordOnline(searchParams: SearchParams): List<SearchResultModel> {
        return searchRepository.searchOnline(searchParams)
    }

    override suspend fun fetchMoreResults(offline: Boolean, dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionModel> {
        if(offline) {
            return this.fetchMoreResultsOffline(dictionaryId, searchType, word, page).map { it.toDefinitionModel() }
        }
        val searchResult = this.fetchMoreResultsOnline(dictionaryId, searchType, word, page)
        return searchResult.definitions
    }

    private suspend fun fetchMoreResultsOffline(dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionEntity> {
        return searchRepository.fetchMoreResultsOffline(dictionaryId, searchType, word, page)
    }

    private suspend fun fetchMoreResultsOnline(dictionaryId: Int, searchType: Int, word: String, page: Int): SearchResultModel {
        return searchRepository.fetchMoreResultsOnline(dictionaryId, searchType, word, page)
    }
}
