package com.ocram.qichwadic.core.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.core.data.model.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * from favorite ORDER BY word, id")
    fun getFavorites(): LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavorite(favorite: FavoriteEntity): Long

    @Delete
    fun removeFavorite(favorite: FavoriteEntity): Int

    @Query("DELETE from favorite")
    fun clearFavorites(): Int
}
