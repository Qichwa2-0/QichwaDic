package com.ocram.qichwadic.data.repository;

import com.ocram.qichwadic.domain.interactor.SearchType;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.domain.repository.SearchRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class SearchRepositoryImpl implements SearchRepository {

    private final SearchLocalDataStore searchLocalDataStore;

    private final SearchCloudDataStore searchCloudDataStore;

    @Inject
    public SearchRepositoryImpl(SearchLocalDataStore searchLocalDataStore, SearchCloudDataStore searchCloudDataStore) {
        this.searchLocalDataStore = searchLocalDataStore;
        this.searchCloudDataStore = searchCloudDataStore;
    }

    @Override
    public Flowable<List<SearchResult>> searchOnline(int fromQuechua, String target, String word, int searchType) {
        return searchCloudDataStore.search(fromQuechua, target, word, searchType);
    }

    @Override
    public Flowable<List<SearchResult>> searchOffline(int fromQuechua, String target, String word, int searchType) {
        String langBegin = "qu";
        String langEnd = target;
        if(fromQuechua == 0) {
            langEnd = langBegin;
            langBegin = target;
        }
        final String queryString = SearchType.buildSearchCriteria(searchType, word);
        return searchLocalDataStore.getDictionariesContainingWord(langBegin, langEnd, queryString)
                .flatMap(dictionaries -> {
                    if(dictionaries.isEmpty()) {
                        return Flowable.just(Collections.emptyList());
                    }
                    Map<Integer, SearchResult> searchResultMap = new HashMap<>();
                    for(SearchResult searchResult : dictionaries) {
                        searchResultMap.put(searchResult.getDictionaryId(), searchResult);
                    }
                    return searchLocalDataStore.findDefinitionsInDictionary(new ArrayList<>(searchResultMap.keySet()), queryString)
                            .map(definitions -> {
                                for(Definition definition : definitions) {
                                    SearchResult searchResult = searchResultMap.get(definition.getDictionaryId());
                                    if(searchResult != null) {
                                        searchResult.addDefinition(definition);
                                    }
                                }
                                List<SearchResult> finalResult = new ArrayList<>(searchResultMap.values());
                                Collections.sort(finalResult);
                                return finalResult;
                            });
                });
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResultsOffline(int dictionaryId, int searchType, String word, int page) {
        return searchLocalDataStore.fetchMoreResults(dictionaryId, word, searchType, page);
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResultsOnline(int dictionaryId, int searchType, String word, int page) {
        return searchCloudDataStore.fetchMoreResults(dictionaryId, word, searchType, page);
    }

    public interface SearchLocalDataStore {

        Flowable<List<SearchResult>> getDictionariesContainingWord(String langBegin, String landEnd, String wordQuery);

        Flowable<List<Definition>> findDefinitionsInDictionary(List<Integer> dictionaryIds, String wordQuery);

        Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int searchType, int page);
    }

    public interface SearchCloudDataStore {

        Flowable<List<SearchResult>> search(int fromQuechua,String target, String word, int searchType);

        Flowable<List<Definition>> fetchMoreResults(int dictionaryId, String word, int searchType, int page);

    }
}
