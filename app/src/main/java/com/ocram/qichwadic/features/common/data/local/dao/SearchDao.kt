package com.ocram.qichwadic.features.common.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.SearchResultEntity

@Dao
interface SearchDao {

    @Query("SELECT de.* FROM definition de JOIN dictionary di ON de.dictionary_id = di.id " + "WHERE di.id = :dictionaryId AND de.word LIKE :searchText ORDER BY de.id LIMIT 20 OFFSET :offset")
    suspend fun findDefinitionsLike(dictionaryId: Int, searchText: String, offset: Int): List<DefinitionEntity>

    @Query("SELECT di.id AS dictionary_id, di.name AS dictionary_name, COUNT(di.id) AS total " +
            "FROM dictionary di JOIN definition de ON di.id = de.dictionary_id " +
            "WHERE di.language_begin = :langBegin " +
            "AND di.language_end = :langEnd " +
            "AND de.word LIKE :word " +
            "GROUP BY di.id ORDER BY di.id")
    suspend fun getDictionariesContainingWord(langBegin: String, langEnd: String, word: String): List<SearchResultEntity>

    @RawQuery(observedEntities = [DefinitionEntity::class])
    suspend fun findDefinitionsInAllDictionaries(query: SupportSQLiteQuery): List<DefinitionEntity>

}
