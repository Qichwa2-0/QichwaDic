package com.ocram.qichwadic.features.favorites.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ocram.qichwadic.core.data.model.FavoriteEntity
import com.ocram.qichwadic.core.data.local.dao.FavoriteDao
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteRepository

class DefaultFavoriteRepository
constructor(private val favoriteDao: FavoriteDao) : FavoriteRepository {

    override fun getFavorites(): LiveData<List<DefinitionModel>> {
        return favoriteDao.getFavorites().map { it.map { it.toDefinitionModel() } }
    }

    override fun addFavorite(favorite: DefinitionModel): Long {
        return favoriteDao.addFavorite(FavoriteEntity.fromDefinitionModel(favorite))
    }

    override fun removeFavorite(favorite: DefinitionModel): Int {
        return favoriteDao.removeFavorite(FavoriteEntity.fromDefinitionModel(favorite))
    }

    override fun clearFavorites(): Int {
        return favoriteDao.clearFavorites()
    }
}
