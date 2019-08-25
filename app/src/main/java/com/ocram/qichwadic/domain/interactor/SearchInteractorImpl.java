package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.domain.repository.DictionaryRepository;
import com.ocram.qichwadic.domain.repository.SearchRepository;
import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchInteractorImpl implements SearchInteractor{

    private SearchRepository searchRepository;
    private DictionaryRepository dictionaryRepository;

    @Inject
    public SearchInteractorImpl(SearchRepository searchRepository, DictionaryRepository dictionaryRepository) {
        this.searchRepository = searchRepository;
        this.dictionaryRepository = dictionaryRepository;
    }

    @Override
    public Flowable<List<SearchResult>> queryWord(int fromQuechua, String target, int searchType, String word){
        return dictionaryRepository.getSavedDictionariesTotal()
                .flatMap(dictionariesTotal -> {
                    if(dictionariesTotal > 0) {
                        return this.findWords(fromQuechua, target, searchType, word);
                    }
                    return Flowable.<List<SearchResult>>just(Collections.emptyList());
                })
                .onErrorReturnItem(Collections.emptyList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Flowable<List<SearchResult>> findWords(int fromQuechua, String target, int searchType, String word) {
        final String queryString = SearchType.buildSearchCriteria(searchType, word);
        String langBegin = "qu";
        String langEnd = target;
        if(fromQuechua == 0) {
            langEnd = langBegin;
            langBegin = target;
        }
        return searchRepository.getDictionariesContainingWord(langBegin, langEnd, queryString)
                .flatMap(dictionaries -> {
                    if(dictionaries.isEmpty()) {
                        return Flowable.just(Collections.emptyList());
                    }
                    Map<Integer, SearchResult> searchResultMap = new HashMap<>();
                    for(SearchResult searchResult : dictionaries) {
                        searchResultMap.put(searchResult.getDictionaryId(), searchResult);
                    }
                    return searchRepository.findDefinitionsInDictionary(new ArrayList<>(searchResultMap.keySet()), queryString)
                            .map(definitions -> {
                                for(Definition definition : definitions) {
                                    SearchResult searchResult = searchResultMap.get(definition.getDictionaryId());
                                    if(searchResult != null) {
                                        searchResult.addDefinition(definition);
                                    }
                                }
                                return new ArrayList<>(searchResultMap.values());
                            });
                });
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResults(int dictionaryId, int searchType, String word, int page) {
        return searchRepository.fetchMoreResults(
                dictionaryId, SearchType.buildSearchCriteria(searchType, word), computeOffset(page)
        );
    }

    private int computeOffset(int page){
        return SearchParams.MAX_SEARCH_RESULTS * (page - 1 );
    }
}
