package com.ocram.qichwadic.features.favorites.data.repository

import com.ocram.qichwadic.core.data.model.FavoriteEntity
import com.ocram.qichwadic.core.data.local.dao.FavoriteDao
import com.ocram.qichwadic.core.data.model.fromDefinitionModel
import com.ocram.qichwadic.core.data.model.toDefinitionModel
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultFavoriteRepository
constructor(private val favoriteDao: FavoriteDao) : FavoriteRepository {

    override suspend fun getFavorites(page: Int): Flow<List<DefinitionModel>> {
        return withContext(Dispatchers.IO) {
            favoriteDao.getFavorites(limit = LIMIT, offset = computeOffset(page))
                .map { list -> list.map(FavoriteEntity::toDefinitionModel) }
        }
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

    private companion object {
        const val LIMIT = 20
        fun computeOffset(page: Int) = (page - 1) * LIMIT
    }
}
