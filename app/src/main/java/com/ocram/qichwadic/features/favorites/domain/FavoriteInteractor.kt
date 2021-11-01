package com.ocram.qichwadic.features.favorites.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel

interface FavoriteInteractor {

    suspend fun getFavorites(): List<DefinitionModel>

    suspend fun addFavorite(definition: DefinitionModel): Boolean

    suspend fun removeFavorite(favorite: DefinitionModel): Boolean

    suspend fun clearFavorites(): Boolean
}

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) : FavoriteInteractor {

    override suspend fun getFavorites(): List<DefinitionModel> {
        return favoriteRepository.getFavorites()
    }

    override suspend fun addFavorite(definition: DefinitionModel): Boolean {
        return favoriteRepository.addFavorite(definition) > 0
    }

    override suspend fun removeFavorite(favorite: DefinitionModel): Boolean {
        return favoriteRepository.removeFavorite(favorite) > 0
    }

    override suspend fun clearFavorites(): Boolean {
        return favoriteRepository.clearFavorites() > 0
    }
}
