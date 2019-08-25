package com.ocram.qichwadic.domain.repository;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;

public interface SearchRepository {

    Flowable<List<SearchResult>> getDictionariesContainingWord(String langBegin, String landEnd, String wordQuery);

    Flowable<List<Definition>> findDefinitionsInDictionary(List<Integer> dictionaryIds, String wordQuery);

    Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String searchCriteria, int page);
}
