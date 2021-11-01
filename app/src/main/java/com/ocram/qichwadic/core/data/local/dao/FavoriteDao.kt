package com.ocram.qichwadic.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.core.data.model.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * from favorite ORDER BY word, id")
    suspend fun getFavorites(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity): Long

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity): Int

    @Query("DELETE from favorite")
    suspend fun clearFavorites(): Int
}
