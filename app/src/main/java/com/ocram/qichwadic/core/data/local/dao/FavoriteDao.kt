package com.ocram.qichwadic.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.core.data.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * from favorite ORDER BY word, id LIMIT :limit OFFSET :offset")
    fun getFavorites(limit: Int = 20, offset: Int = 0): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorites(vararg favorite: FavoriteEntity): List<Long>

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity): Int

    @Query("DELETE from favorite")
    suspend fun clearFavorites(): Int
}
