package com.ocram.qichwadic.features.favorites.domain

import com.ocram.qichwadic.core.domain.model.DefinitionModel

interface FavoriteRepository {

    fun getFavorites(): List<DefinitionModel>

    fun addFavorite(favorite: DefinitionModel): Long

    fun removeFavorite(favorite: DefinitionModel): Int

    fun clearFavorites(): Int

}