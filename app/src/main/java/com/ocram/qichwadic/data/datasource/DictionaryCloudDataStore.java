package com.ocram.qichwadic.data.datasource;

import com.ocram.qichwadic.data.repository.DictionaryRepositoryImpl;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.framework.net.client.RetrofitClient;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class DictionaryCloudDataStore implements DictionaryRepositoryImpl.CloudDataStore {

    private final RetrofitClient retrofitClient;

    @Inject
    public DictionaryCloudDataStore(RetrofitClient retrofitClient) {
        this.retrofitClient = retrofitClient;
    }

    @Override
    public Flowable<List<Dictionary>> getDictionaries() {
        return retrofitClient.getDictionaries();
    }

    @Override
    public Flowable<List<Definition>> getDefinitionsByDictionary(String entriesUrl) {
        return retrofitClient.getAllDefinitionsByDictionary(entriesUrl);
    }
}
