package com.ocram.qichwadic.presentation.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;
import android.util.Log;

import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.domain.interactor.DictionaryInteractor;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.domain.interactor.FavoriteInteractor;
import com.ocram.qichwadic.domain.interactor.SearchInteractor;
import com.ocram.qichwadic.framework.di.viewmodel.DaggerViewModelComponent;
import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchViewState;
import com.ocram.qichwadic.framework.preferences.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SearchViewModel extends AndroidViewModel {

    @Inject
    SearchInteractor searchInteractor;

    @Inject
    FavoriteInteractor favoriteInteractor;

    @Inject
    DictionaryInteractor dictionaryInteractor;

    private PreferencesHelper preferencesHelper;

    private MutableLiveData<List<SearchResult>> resultLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Definition>> extraDefinitions = new MutableLiveData<>();
    private MutableLiveData<SearchViewState> searchViewState = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadFetchMore = new MutableLiveData<>();
    private MutableLiveData<Boolean> saveFavoriteResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> offlineSearch = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SearchViewModel(Application application) {
        super(application);
        DaggerViewModelComponent
                .builder()
                .appComponent(((QichwaDicApplication)application).getAppComponent())
                .build()
                .inject(this);
        preferencesHelper = ((QichwaDicApplication)application).getAppComponent().getPreferencesHelper();
        loadSearchModeConfig();
        searchViewState.setValue(new SearchViewState());
    }

    private void loadSearchModeConfig() {
        compositeDisposable.add(
                preferencesHelper.getSearchMode().subscribe(isOffline -> offlineSearch.postValue(isOffline))
        );

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public MutableLiveData<List<SearchResult>> getResultLiveData() {
        return resultLiveData;
    }

    public MutableLiveData<SearchViewState> getSearchViewState() {
        return searchViewState;
    }

    public MutableLiveData<List<Definition>> getExtraDefinitions() {
        return extraDefinitions;
    }

    public MutableLiveData<Boolean> getLoadFetchMore() {
        return loadFetchMore;
    }

    public MutableLiveData<Boolean> getSaveFavoriteResult() {
        return saveFavoriteResult;
    }

    public LiveData<Boolean> getOfflineSearch() {
        return offlineSearch;
    }

    public void searchWord(int fromQuechua, String target, int searchType, String word){
        if(searchViewState.getValue() != null) {
            searchViewState.getValue().setLoading(true);
            searchViewState.setValue(searchViewState.getValue());
        }
        preferencesHelper.saveSearchParams(target, fromQuechua == 1, searchType);

        compositeDisposable.add(
                searchInteractor
                        .queryWord(fromQuechua, target, searchType, word, isOfflineSearch())
                        .subscribe(this::onSearchSuccess, this::onSearchError)
        );
    }

    private void onSearchSuccess(List<SearchResult> searchResults){
        if(searchViewState.getValue() != null) {
            searchViewState.getValue().setLoading(false);
            searchViewState.getValue().setHasError(false);
            searchViewState.setValue(searchViewState.getValue());
        }
        resultLiveData.postValue(searchResults);
    }

    private void onSearchError(Throwable throwable){
        Log.e("ERROR", "error searching", throwable);
        if(searchViewState.getValue() != null) {
            searchViewState.getValue().setHasError(true);
            searchViewState.getValue().setLoading(false);
            searchViewState.setValue(searchViewState.getValue());
        }
    }

    public void fetchMoreResults(int dictionaryId, int searchType, String word, int page){
        loadFetchMore.setValue(true);
        compositeDisposable.add(
                searchInteractor
                        .fetchMoreResults(dictionaryId, searchType, word, page, isOfflineSearch())
                        .subscribe(this::onFetchMoreSuccess)
        );
    }

    private boolean isOfflineSearch() {
        return getOfflineSearch().getValue() != null && getOfflineSearch().getValue();
    }

    private void onFetchMoreSuccess(List<Definition> definitions){
        loadFetchMore.postValue(false);
        extraDefinitions.postValue(definitions);
    }

    public void saveFavorite(Definition definition){
        compositeDisposable.add(
                favoriteInteractor
                        .addFavorite(definition)
                        .subscribe(result -> saveFavoriteResult.postValue(result))
        );
    }

    public void changeSearchModeConfig(boolean isOffline) {
        preferencesHelper.saveOfflineSearchMode(isOffline)
                .doOnComplete(() -> offlineSearch.postValue(isOffline))
                .subscribe();
    }
}
