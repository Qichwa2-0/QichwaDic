package com.ocram.qichwadic.framework.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ocram.qichwadic.domain.model.Favorite;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FavoriteDao {

    @Query("SELECT * from favorite ORDER BY word, id")
    Flowable<List<Favorite>> getFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(Favorite favorite);

    @Delete
    void removeFavorite(Favorite favorite);

    @Query("DELETE from favorite")
    void clearFavorites();
}
