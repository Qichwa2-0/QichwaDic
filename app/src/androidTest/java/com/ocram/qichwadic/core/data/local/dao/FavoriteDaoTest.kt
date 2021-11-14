package com.ocram.qichwadic.core.data.local.dao

import com.ocram.qichwadic.core.data.model.FavoriteEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FavoriteDaoTest: BaseDbTest() {

    @Test
    fun testAddFavorites() {
        val total = 1
        val favorite = generateFavorites(total).first()
        val favorites: List<FavoriteEntity>
        runBlocking {
            db.favoriteDao().addFavorites(favorite)
            favorites = db.favoriteDao().getFavorites()
        }
        Assert.assertEquals(favorite, favorites.first())
    }

    @Test
    fun testGetFavorites() {
        val total = 20
        val favorite = generateFavorites(total)
        val favorites: List<FavoriteEntity>
        runBlocking {
            db.favoriteDao().addFavorites(*favorite.toTypedArray())
            favorites = db.favoriteDao().getFavorites()
        }
        Assert.assertEquals(total, favorites.size)
    }

    @Test
    fun testRemoveFavorite() {
        val total = 20
        val favorite = generateFavorites(total)

        var savedFavorites: List<FavoriteEntity>
        val totalSaved: Int
        val totalSavedAfterRemove: Int
        runBlocking {
            db.favoriteDao().addFavorites(*favorite.toTypedArray())
            savedFavorites = db.favoriteDao().getFavorites()
            totalSaved = savedFavorites.size

            db.favoriteDao().removeFavorite(savedFavorites[5])
            db.favoriteDao().removeFavorite(savedFavorites[10])
            totalSavedAfterRemove = db.favoriteDao().getFavorites().size
        }
        Assert.assertEquals(totalSavedAfterRemove, totalSaved - 2)
    }

    @Test
    fun testClearFavorites() {
        val total = 20
        val favorite = generateFavorites(total)

        val totalSaved: Int
        val totalSavedAfterRemove: Int
        runBlocking {
            totalSaved = db.favoriteDao().addFavorites(*favorite.toTypedArray()).size
            db.favoriteDao().clearFavorites()
            totalSavedAfterRemove = db.favoriteDao().getFavorites().size
        }
        Assert.assertEquals(total, totalSaved)
        Assert.assertEquals(0, totalSavedAfterRemove)
    }

    private fun generateFavorites(total: Int = 5): List<FavoriteEntity> {
        return (1..total).map { counter ->
            FavoriteEntity(
                id = counter,
                word = "Word $counter",
                meaning = "Meaning $counter",
                dictionaryId = 1,
                dictionaryName = "Dictionary"
            )
        }
    }
}