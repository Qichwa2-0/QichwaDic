package com.ocram.qichwadic.data.datasource;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.ocram.qichwadic.data.repository.SearchRepositoryImpl;
import com.ocram.qichwadic.domain.interactor.SearchType;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.framework.dao.SearchDao;
import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchParams;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class SearchLocalDataStore implements SearchRepositoryImpl.SearchLocalDataStore {

    private final SearchDao searchDao;

    @Inject
    public SearchLocalDataStore(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    @Override
    public Flowable<List<SearchResult>> getDictionariesContainingWord(String langBegin, String landEnd, String wordQuery) {
        return searchDao.getDictionariesContainingWord(langBegin, landEnd, wordQuery);
    }

    @Override
    public Flowable<List<Definition>> findDefinitionsInDictionary(List<Integer> dictionaryIds, String wordQuery) {
        return searchDao.findDefinitionsInAllDictionaries(buildCompatibleWritingWord(dictionaryIds, wordQuery));
    }

    private SimpleSQLiteQuery buildCompatibleWritingWord(List<Integer> dictionaryIds, String word) {
        String query =
                "SELECT * FROM " +
                        "(SELECT de.* " +
                        "FROM definition de INNER JOIN dictionary di ON de.dictionary_id = di.id " +
                        "WHERE di.id = %s AND de.word LIKE \"%s\" " +
                        "ORDER BY de.id LIMIT 20)";
        StringBuilder queryBuilder = new StringBuilder();
        for(int i = 0; i < dictionaryIds.size(); i++) {
            queryBuilder.append(String.format(query, dictionaryIds.get(i), word));
            if(i< dictionaryIds.size() -1) {
                queryBuilder.append(" UNION ALL ");
            }
        }
        return new SimpleSQLiteQuery(queryBuilder.toString());
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int searchType, int page) {
        return searchDao.findDefinitionsLike(dictionaryId, SearchType.buildSearchCriteria(searchType, word), computeOffset(page));
    }

    private int computeOffset(int page){
        return SearchParams.MAX_SEARCH_RESULTS * (page - 1 );
    }
}
