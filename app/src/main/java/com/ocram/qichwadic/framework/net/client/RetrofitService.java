package com.ocram.qichwadic.framework.net.client;

import com.ocram.qichwadic.BuildConfig;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.SearchResult;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    String API_URL = BuildConfig.SERVER_URL;
    String ALL_DICTIONARIES = API_URL + "dictionaries";
    String SEARCH_WORDS = API_URL + "words";

    @GET(ALL_DICTIONARIES)
    Flowable<List<Dictionary>> getDictionaries(@Query("all") int all);

    @GET(SEARCH_WORDS)
    Flowable<List<SearchResult>> searchWord(
            @Query("all") int all,
            @Query("searchWord") String searchWord,
            @Query("fromQuechua") int fromQuechua,
            @Query("target") String target,
            @Query("searchType") int searchType
    );

    @GET(ALL_DICTIONARIES + "/{dictionaryId}/words/fetch")
    Flowable<List<Definition>> fetchMoreResults(
            @Path("dictionaryId") int dictionaryId,
            @Query("searchWord") String searchWord,
            @Query("fromQuechua") int fromQuechua,
            @Query("searchType") int searchType,
            @Query("page") int page
    );

    @GET(ALL_DICTIONARIES + "/{dictionaryId}/words")
    Flowable<List<Definition>> getAllDefinitionsByDictionary(@Path("dictionaryId") int dictionaryId);
}
