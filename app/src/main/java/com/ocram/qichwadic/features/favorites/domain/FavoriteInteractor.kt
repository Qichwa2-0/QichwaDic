package com.ocram.qichwadic.features.favorites.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FavoriteInteractor {

    suspend fun getFavorites(): List<DefinitionModel>

    fun addFavorite(definition: DefinitionModel): Boolean

    fun removeFavorite(favorite: DefinitionModel): Boolean

    fun clearFavorites(): Boolean
}

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) : FavoriteInteractor {

    override suspend fun getFavorites():List<DefinitionModel> {
        var favorites: List<DefinitionModel>
        withContext(Dispatchers.IO) {
            favorites = favoriteRepository.getFavorites()
        }
        return favorites
    }

    override fun addFavorite(definition: DefinitionModel): Boolean {
        return favoriteRepository.addFavorite(definition) > 0
    }

    override fun removeFavorite(favorite: DefinitionModel): Boolean {
        return favoriteRepository.removeFavorite(favorite) > 0
    }

    override fun clearFavorites(): Boolean {
        return favoriteRepository.clearFavorites() > 0
    }
}
