package com.ocram.qichwadic.features.search.domain

import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.domain.DefinitionModel
import com.ocram.qichwadic.features.common.domain.SearchResultModel
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository

interface SearchInteractor {

    suspend fun queryWord(offline: Boolean, fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel>

    suspend fun queryWordOffline(fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel>

    suspend fun queryWordOnline(fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel>

    suspend fun fetchMoreResults(offline: Boolean, dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionModel>

}

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {

    override suspend fun queryWord(offline: Boolean, fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel> {
        if(offline) {
            return this.queryWordOffline(fromQuechua, target, searchType, word)
        }
        return queryWordOnline(fromQuechua, target, searchType, word)
    }

    override suspend fun queryWordOffline(fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel> {
        return searchRepository.searchOffline(fromQuechua, target, word, searchType)
    }

    override suspend fun queryWordOnline(fromQuechua: Int, target: String, searchType: Int, word: String): List<SearchResultModel> {
        return searchRepository.searchOnline(fromQuechua, target, word, searchType)
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
