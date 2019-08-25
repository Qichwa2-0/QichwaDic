package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Favorite;
import com.ocram.qichwadic.domain.repository.FavoriteRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoriteInteractorImpl implements FavoriteInteractor {

    private FavoriteRepository favoriteRepository;

    @Inject
    public FavoriteInteractorImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Observable<Boolean> addFavorite(Definition definition) {
        return Observable.<Boolean>create(e -> {
            favoriteRepository.addFavorite(new Favorite(definition));
            e.onNext(true);
        }).onErrorReturnItem(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFavorite(Favorite favorite) {
        return Observable.<Boolean>create(e -> {
            favoriteRepository.removeFavorite(favorite);
            e.onNext(true);
        }).onErrorReturnItem(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> clearFavorites() {
        return Observable.<Boolean>create(e -> {
            favoriteRepository.clearFavorites();
            e.onNext(true);
        }).onErrorReturnItem(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Flowable<List<Favorite>> getFavorites() {
        return favoriteRepository.getFavorites().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
