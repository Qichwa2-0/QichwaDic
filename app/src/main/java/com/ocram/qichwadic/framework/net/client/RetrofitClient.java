package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class RetrofitClient implements Service {

    private RetrofitService retrofitService;

    @Inject
    public RetrofitClient(RetrofitService retrofitService) {
        this.retrofitService = retrofitService;
    }

    @Override
    public Flowable<List<Dictionary>> getDictionaries() {
        return this.retrofitService.getDictionaries(1);
    }

    @Override
    public Flowable<List<SearchResult>> searchWord(String searchWord, int fromQuechua, String target, int searchType) {
        return retrofitService.searchWord(1, searchWord, fromQuechua, target, searchType);
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int fromQuechua, int searchType, int page) {
        return this.retrofitService.fetchMoreResults(dictionaryId, word, fromQuechua, searchType, page);
    }

    @Override
    public Flowable<List<Definition>> getAllDefinitionsByDictionary(int dictionaryId) {
        return this.retrofitService.getAllDefinitionsByDictionary(dictionaryId);
    }
}
