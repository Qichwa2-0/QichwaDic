package com.ocram.qichwadic.data.datasource;

import com.ocram.qichwadic.data.repository.SearchRepositoryImpl;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.framework.net.client.RetrofitClient;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class SearchCloudDataStore implements SearchRepositoryImpl.SearchCloudDataStore {

    private final RetrofitClient retrofitClient;

    @Inject
    public SearchCloudDataStore(RetrofitClient retrofitClient) {
        this.retrofitClient = retrofitClient;
    }

    @Override
    public Flowable<List<SearchResult>> search(int fromQuechua,String target, String word, int searchType) {
        return retrofitClient.searchWord(word, fromQuechua, target, searchType);
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int searchType, int page) {
        return retrofitClient.fetchMoreResults(dictionaryId, word, 0, searchType, page);
    }
}
