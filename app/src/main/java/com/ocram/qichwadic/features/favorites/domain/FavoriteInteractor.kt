package com.ocram.qichwadic.features.favorites.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import java.lang.Exception

interface FavoriteInteractor {

    fun getFavorites(): LiveData<List<DefinitionModel>>

    fun addFavorite(definition: DefinitionModel): Boolean

    fun removeFavorite(favorite: DefinitionModel): Boolean

    fun clearFavorites(): Boolean
}

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) : FavoriteInteractor {

    override fun getFavorites(): LiveData<List<DefinitionModel>> {
        return try {
            favoriteRepository.getFavorites()
        } catch (_: Exception) {
            liveData {
                emit(emptyList())
            }
        }
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
