package com.ocram.qichwadic.domain.interactor;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Favorite;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface FavoriteInteractor {

    Observable<Boolean> addFavorite(Definition definition);

    Observable<Boolean> removeFavorite(Favorite favorite);

    Observable<Boolean> clearFavorites();

    Flowable<List<Favorite>> getFavorites();
}
