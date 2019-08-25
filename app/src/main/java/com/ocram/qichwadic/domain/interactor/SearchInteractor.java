package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;

public interface SearchInteractor {

    Flowable<List<SearchResult>> queryWord(int fromQuechua, String target, int searchType, String word);

    Flowable<List<Definition>> fetchMoreResults(int dictionaryId, int searchType, String word, int page);
}
