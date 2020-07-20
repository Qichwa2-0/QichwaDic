package com.ocram.qichwadic.features.search.data.datastore

import androidx.sqlite.db.SimpleSQLiteQuery

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.local.dao.SearchDao
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import com.ocram.qichwadic.features.search.data.SearchType

interface SearchLocalDataStore {

    suspend fun getDictionariesContainingWord(langBegin: String, landEnd: String, wordQuery: String): List<SearchResultEntity>

    suspend fun findDefinitionsInDictionary(dictionaryIds: List<Int>, wordQuery: String): List<DefinitionEntity>

    suspend fun fetchMoreResults(dictionaryId: Int, word: String, searchType: Int, page: Int): List<DefinitionEntity>
}

class SearchLocalDataStoreImpl(private val searchDao: SearchDao): SearchLocalDataStore {

    private val MAX_SEARCH_RESULTS = 20

    override suspend fun getDictionariesContainingWord(langBegin: String, landEnd: String, wordQuery: String): List<SearchResultEntity> {
        return searchDao.getDictionariesContainingWord(langBegin, landEnd, wordQuery)
    }

    override suspend fun findDefinitionsInDictionary(dictionaryIds: List<Int>, wordQuery: String): List<DefinitionEntity> {
        return searchDao.findDefinitionsInAllDictionaries(buildCompatibleWritingWord(dictionaryIds, wordQuery))
    }

    private fun buildCompatibleWritingWord(dictionaryIds: List<Int>, word: String): SimpleSQLiteQuery {
        val query = "SELECT * FROM " +
                "(SELECT de.* " +
                "FROM definition de INNER JOIN dictionary di ON de.dictionary_id = di.id " +
                "WHERE di.id = %s AND de.word LIKE \"%s\" " +
                "ORDER BY de.id LIMIT 20)"
        val queryBuilder = StringBuilder()
        for (i in dictionaryIds.indices) {
            queryBuilder.append(String.format(query, dictionaryIds[i], word))
            if (i < dictionaryIds.size - 1) {
                queryBuilder.append(" UNION ALL ")
            }
        }
        return SimpleSQLiteQuery(queryBuilder.toString())
    }

    override suspend fun fetchMoreResults(dictionaryId: Int, word: String, searchType: Int, page: Int): List<DefinitionEntity> {
        return searchDao.findDefinitionsLike(dictionaryId, SearchType.buildSearchCriteria(searchType, word), computeOffset(page))
    }

    private fun computeOffset(page: Int): Int {
        return MAX_SEARCH_RESULTS * (page - 1)
    }
}
