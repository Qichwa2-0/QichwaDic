package com.ocram.qichwadic.domain.repository;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;

public interface SearchRepository {

    Flowable<List<SearchResult>> searchOnline(int fromQuechua, String target, String word, int searchType);

    Flowable<List<SearchResult>> searchOffline(int fromQuechua, String target, String word, int searchType);

    Flowable<List<Definition>> fetchMoreResultsOffline(int dictionaryId, int searchType, String word, int page);

    Flowable<List<Definition>> fetchMoreResultsOnline(int dictionaryId, int searchType, String word, int page);
}
