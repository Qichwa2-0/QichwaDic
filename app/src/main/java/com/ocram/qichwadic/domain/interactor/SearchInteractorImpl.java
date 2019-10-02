package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.domain.repository.SearchRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchInteractorImpl implements SearchInteractor{

    private SearchRepository searchRepository;

    @Inject
    public SearchInteractorImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public Flowable<List<SearchResult>> queryWord(int fromQuechua, String target, int searchType, String word, boolean offline){
        Flowable<List<SearchResult>> flowable =
                offline ? searchRepository.searchOffline(fromQuechua, target, word, searchType)
                        : searchRepository.searchOnline(fromQuechua, target, word, searchType) ;
        return flowable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<List<Definition>> fetchMoreResults(int dictionaryId, int searchType, String word, int page, boolean offline) {
        Flowable<List<Definition>> flowable =
                offline ? searchRepository.fetchMoreResultsOffline(dictionaryId, searchType, word, page)
                        : searchRepository.fetchMoreResultsOnline(dictionaryId, searchType, word, page);
        return flowable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
