package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.BuildConfig;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface RetrofitService {

    String API_URL = BuildConfig.SERVER_URL;
    String ALL_DICTIONARIES = API_URL + "dictionaries.json";

    @GET(ALL_DICTIONARIES)
    Flowable<List<Dictionary>> getDictionaries();

    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    @GET(API_URL + "/{entryUrl}")
    Flowable<List<Definition>> getAllDefinitionsByDictionary(@Path("entryUrl") String entryUrl);
}
