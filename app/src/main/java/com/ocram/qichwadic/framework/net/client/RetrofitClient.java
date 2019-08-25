package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RetrofitClient implements Service {

    private RetrofitService retrofitService;

    @Inject
    public RetrofitClient(RetrofitService retrofitService) {
        this.retrofitService = retrofitService;
    }

    @Override
    public Flowable<List<Dictionary>> getDictionaries() {
        return this.retrofitService.getDictionaries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<List<Definition>> getAllDefinitionsByDictionary(String entriesUrl) {
        return this.retrofitService.getAllDefinitionsByDictionary(entriesUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
