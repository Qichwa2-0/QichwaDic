package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;

public interface Service {

    Flowable<List<Dictionary>> getDictionaries();

    Flowable<List<SearchResult>> searchWord(String searchWord, int fromQuechua, String target, int searchType);

    Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int fromQuechua, int searchType, int page);

    Flowable<List<Definition>> getAllDefinitionsByDictionary(int dictionaryId);
}
