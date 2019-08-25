package com.ocram.qichwadic.domain.repository;

import com.ocram.qichwadic.domain.model.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public interface FavoriteRepository {

    void addFavorite(Favorite favorite);

    void removeFavorite(Favorite favorite);

    void clearFavorites();

    Flowable<List<Favorite>> getFavorites();

}
