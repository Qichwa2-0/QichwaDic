package com.ocram.qichwadic.features.favorites.data.repository

import com.ocram.qichwadic.core.data.model.FavoriteEntity
import com.ocram.qichwadic.core.data.local.dao.FavoriteDao
import com.ocram.qichwadic.core.data.model.fromDefinitionModel
import com.ocram.qichwadic.core.data.model.toDefinitionModel
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteRepository

class DefaultFavoriteRepository
constructor(private val favoriteDao: FavoriteDao) : FavoriteRepository {

    override suspend fun getFavorites(): List<DefinitionModel> {
        return favoriteDao.getFavorites().map { it.toDefinitionModel()  }
    }

    override suspend fun addFavorites(favorite: DefinitionModel): List<Long> {
        return favoriteDao.addFavorites(FavoriteEntity.fromDefinitionModel(favorite))
    }

    override suspend fun removeFavorite(favorite: DefinitionModel): Int {
        return favoriteDao.removeFavorite(FavoriteEntity.fromDefinitionModel(favorite))
    }

    override suspend fun clearFavorites(): Int {
        return favoriteDao.clearFavorites()
    }
}
