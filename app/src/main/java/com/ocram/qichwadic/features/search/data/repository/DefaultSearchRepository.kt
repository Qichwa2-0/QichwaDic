package com.ocram.qichwadic.features.search.data.repository

import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.toDefinitionModel
import com.ocram.qichwadic.core.data.model.toSearchResultModel
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.domain.model.SearchParams
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.features.search.data.datastore.SearchCloudDataStore
import com.ocram.qichwadic.features.search.data.SearchType
import com.ocram.qichwadic.features.search.data.datastore.SearchLocalDataStore
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchLocalDataStore: SearchLocalDataStore,
    private val searchCloudDataStore: SearchCloudDataStore,
    private val preferencesHelper: PreferencesHelper
) : SearchRepository {

    override suspend fun search(searchParams: SearchParams): List<SearchResultModel> {
        return if (preferencesHelper.isOfflineSearchMode()) {
            searchOffline(searchParams)
        } else {
            searchOnline(searchParams)
        }
    }

    private suspend fun searchOnline(searchParams: SearchParams): List<SearchResultModel> {
        val response = searchCloudDataStore.search(
            if (searchParams.isFromQuechua) 1 else 0,
            searchParams.nonQuechuaLangCode,
            searchParams.searchWord,
            searchParams.searchTypePos
        )
        return when (response) {
            is ApiResponse.Success -> response.data.map { it.toSearchResultModel() }
            else -> emptyList()
        }
    }

    private suspend fun searchOffline(searchParams: SearchParams): List<SearchResultModel> {
        var langBegin = "qu"
        var langEnd = searchParams.nonQuechuaLangCode
        if (!searchParams.isFromQuechua) {
            langEnd = langBegin
            langBegin = searchParams.nonQuechuaLangCode
        }
        val queryString = SearchType.buildSearchCriteria(searchParams.searchTypePos, searchParams.searchWord)

        val searchResults = searchLocalDataStore.getDictionariesContainingWord(langBegin, langEnd, queryString)

        if(searchResults.isNotEmpty()) {

            val searchResultMap = searchResults.associateBy { it.dictionaryId }

            val definitionsByDictionaryId = searchLocalDataStore.findDefinitionsInDictionary(
                searchResultMap.keys, queryString
            ).groupBy(DefinitionEntity::dictionaryId)

            searchResultMap.entries.forEach { entry ->
                entry.value.definitions.addAll(definitionsByDictionaryId[entry.key] ?: emptyList())
            }
            return searchResultMap.values.map { it.toSearchResultModel() }
        }
        return emptyList()
    }

    override suspend fun fetchMoreResults(
        dictionaryId: Int,
        searchType: Int,
        word: String,
        page: Int
    ): List<DefinitionModel> {
        return if (preferencesHelper.isOfflineSearchMode()) {
            fetchMoreResultsOffline(dictionaryId, searchType, word, page)
        } else {
            fetchMoreResultsOnline(dictionaryId, searchType, word, page)
        }
    }

    private suspend fun fetchMoreResultsOffline(
        dictionaryId: Int,
        searchType: Int,
        word: String,
        page: Int
    ): List<DefinitionModel> {
        return searchLocalDataStore
            .fetchMoreResults(dictionaryId, word, searchType, page)
            .map { it.toDefinitionModel() }
    }

    private suspend fun fetchMoreResultsOnline(
        dictionaryId: Int,
        searchType: Int,
        word: String,
        page: Int
    ): List<DefinitionModel> {
        return when (
            val response = searchCloudDataStore.fetchMoreResults(dictionaryId, word, searchType, page)
        ) {
            is ApiResponse.Success -> response.data.definitions.map { it.toDefinitionModel() }
            else -> emptyList()
        }
    }
}
