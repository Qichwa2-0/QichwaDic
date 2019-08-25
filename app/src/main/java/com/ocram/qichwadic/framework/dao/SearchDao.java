package com.ocram.qichwadic.framework.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface SearchDao {

    @Query("SELECT de.* FROM definition de JOIN dictionary di ON de.dictionary_id = di.id " +
            "WHERE di.id = :dictionaryId AND de.word LIKE :searchText ORDER BY de.id LIMIT 20 OFFSET :offset")
    Flowable<List<Definition>> findDefinitionsLike(int dictionaryId, String searchText, int offset);

    @Query("SELECT di.id AS dictionary_id, di.name AS dictionary_name, COUNT(di.id) AS total " +
            "FROM dictionary di JOIN definition de ON di.id = de.dictionary_id " +
            "WHERE di.language_begin = :langBegin " +
            "AND di.language_end = :langEnd " +
            "AND de.word LIKE :word GROUP BY di.id")
    Flowable<List<SearchResult>> getDictionariesContainingWord(String langBegin, String langEnd, String word);

    @RawQuery(observedEntities = Definition.class)
    Flowable<List<Definition>> findDefinitionsInAllDictionaries(SupportSQLiteQuery query);

}
