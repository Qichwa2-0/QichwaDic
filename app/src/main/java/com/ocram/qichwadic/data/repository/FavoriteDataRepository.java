package com.ocram.qichwadic.data.repository;

import com.ocram.qichwadic.domain.model.Favorite;
import com.ocram.qichwadic.domain.repository.FavoriteRepository;
import com.ocram.qichwadic.framework.dao.FavoriteDao;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class FavoriteDataRepository implements FavoriteRepository {

    private FavoriteDao favoriteDao;

    @Inject
    public FavoriteDataRepository(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }

    @Override
    public void addFavorite(Favorite favorite) {
        favoriteDao.addFavorite(favorite);
    }

    @Override
    public void removeFavorite(Favorite favorite) {
        favoriteDao.removeFavorite(favorite);
    }

    @Override
    public void clearFavorites() {
        favoriteDao.clearFavorites();
    }

    @Override
    public Flowable<List<Favorite>> getFavorites() {
        return favoriteDao.getFavorites();
    }
}
