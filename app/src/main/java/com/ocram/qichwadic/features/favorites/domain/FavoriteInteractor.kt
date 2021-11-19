package com.ocram.qichwadic.features.favorites.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    suspend fun getFavorites(page: Int): Flow<List<DefinitionModel>>

    suspend fun addFavorite(definition: DefinitionModel): Boolean

    suspend fun removeFavorite(favorite: DefinitionModel): Boolean

    suspend fun clearFavorites(): Boolean
}

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) : FavoriteInteractor {

    override suspend fun getFavorites(page: Int): Flow<List<DefinitionModel>> {
        return favoriteRepository.getFavorites(page)
    }

    override suspend fun addFavorite(definition: DefinitionModel): Boolean {
        return favoriteRepository.addFavorites(definition).isNotEmpty()
    }

    override suspend fun removeFavorite(favorite: DefinitionModel): Boolean {
        return favoriteRepository.removeFavorite(favorite) > 0
    }

    override suspend fun clearFavorites(): Boolean {
        return favoriteRepository.clearFavorites() > 0
    }
}
