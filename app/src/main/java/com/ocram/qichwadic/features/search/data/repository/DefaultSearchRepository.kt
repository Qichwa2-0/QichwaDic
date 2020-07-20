package com.ocram.qichwadic.features.search.data.repository

import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.features.search.data.datastore.SearchCloudDataStore
import com.ocram.qichwadic.features.search.data.SearchType
import com.ocram.qichwadic.features.search.data.datastore.SearchLocalDataStore
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository

class SearchRepositoryImpl
constructor(private val searchLocalDataStore: SearchLocalDataStore,
            private val searchCloudDataStore: SearchCloudDataStore) : SearchRepository {

    override suspend fun searchOnline(fromQuechua: Int, target: String, word: String, searchType: Int): List<SearchResultModel> {
        return when (val response = searchCloudDataStore.search(fromQuechua, target, word, searchType)) {
            is ApiResponse.Success -> response.data.map { it.toSearchResultModel() }
            else -> emptyList()
        }
    }

    override suspend fun searchOffline(fromQuechua: Int, target: String, word: String, searchType: Int): List<SearchResultModel> {
        var langBegin = "qu"
        var langEnd = target
        if (fromQuechua == 0) {
            langEnd = langBegin
            langBegin = target
        }
        val queryString = SearchType.buildSearchCriteria(searchType, word)

        val searchResults = searchLocalDataStore.getDictionariesContainingWord(langBegin, langEnd, queryString)

        if(searchResults.isNotEmpty()) {

            val searchResultMap = mutableMapOf<Int, SearchResultEntity>()
            searchResults.forEach { searchResultMap.put(it.dictionaryId, it) }


            val definitions = searchLocalDataStore.findDefinitionsInDictionary(searchResultMap.keys.toList(), queryString)
            val definitionsByDictionaryId = definitions.groupBy { it.dictionaryId }

            definitionsByDictionaryId.keys.forEach { dictionaryId ->
                searchResultMap[dictionaryId]?.let { searchResult ->
                    definitionsByDictionaryId[dictionaryId]?.let { searchResult.definitions.addAll(it) }
                }
            }

            val finalResult = mutableListOf<SearchResultModel>()
            finalResult.addAll(searchResultMap.values.map { it.toSearchResultModel() })
            return finalResult
        }

        return emptyList()

    }

    override suspend fun fetchMoreResultsOffline(dictionaryId: Int, searchType: Int, word: String, page: Int): List<DefinitionEntity> {
        return searchLocalDataStore.fetchMoreResults(dictionaryId, word, searchType, page)
    }

    override suspend fun fetchMoreResultsOnline(dictionaryId: Int, searchType: Int, word: String, page: Int): SearchResultModel {
        return when (val response = searchCloudDataStore.fetchMoreResults(dictionaryId, word, searchType, page)) {
            is ApiResponse.Success -> response.data.toSearchResultModel()
            else -> SearchResultModel.empty()
        }
    }

}
