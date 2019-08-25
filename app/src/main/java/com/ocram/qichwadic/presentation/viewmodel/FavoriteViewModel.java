package com.ocram.qichwadic.presentation.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;

import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.domain.model.Favorite;
import com.ocram.qichwadic.domain.interactor.FavoriteInteractor;
import com.ocram.qichwadic.framework.di.viewmodel.DaggerViewModelComponent;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class FavoriteViewModel extends AndroidViewModel {

    @Inject
    FavoriteInteractor favoriteInteractor;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Favorite>> favoriteLiveData;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> deleteFavoriteResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearFavoriteResult = new MutableLiveData<>();

    public FavoriteViewModel(Application application) {
        super(application);
        DaggerViewModelComponent
                .builder()
                .appComponent(((QichwaDicApplication)application).getAppComponent())
                .build()
                .inject(this);
        isLoading.setValue(true);
        this.favoriteLiveData = LiveDataReactiveStreams.fromPublisher(
                favoriteInteractor.getFavorites()
                .onErrorReturnItem(Collections.emptyList())
                .doOnNext(favorites -> isLoading.postValue(false))
        );
    }

    public LiveData<List<Favorite>> getFavoriteLiveData() {
        return favoriteLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getDeleteFavoriteResult() {
        return deleteFavoriteResult;
    }

    public MutableLiveData<Boolean> getClearFavoriteResult() {
        return clearFavoriteResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public void removeFavorite(Favorite favorite){
        compositeDisposable.add(
                favoriteInteractor
                        .removeFavorite(favorite)
                        .doOnError(throwable -> onError(throwable, false))
                        .subscribe(result -> deleteFavoriteResult.postValue(result))
        );
    }

    public void clearFavorites(){
        if(favoriteLiveData.getValue() == null || favoriteLiveData.getValue().isEmpty()){
            return;
        }
        compositeDisposable.add(
                favoriteInteractor.clearFavorites()
                        .doOnError(throwable -> onError(throwable, true))
                        .subscribe(result -> clearFavoriteResult.postValue(result))
        );
    }

    private void onError(Throwable throwable, boolean isClearOp) {
        throwable.printStackTrace();
        if(isClearOp) {
            clearFavoriteResult.postValue(false);
        } else {
            deleteFavoriteResult.postValue(false);
        }
    }
}
