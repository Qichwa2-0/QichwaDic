package com.ocram.qichwadic.features.favorites.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun getFavorites(page: Int): Flow<List<DefinitionModel>>

    suspend fun addFavorites(favorite: DefinitionModel): List<Long>

    suspend fun removeFavorite(favorite: DefinitionModel): Int

    suspend fun clearFavorites(): Int

}