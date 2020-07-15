package com.ocram.qichwadic.features.favorites.domain

import androidx.lifecycle.LiveData
import com.ocram.qichwadic.features.common.domain.DefinitionModel

interface FavoriteRepository {

    fun getFavorites(): LiveData<List<DefinitionModel>>

    fun addFavorite(favorite: DefinitionModel): Long

    fun removeFavorite(favorite: DefinitionModel): Int

    fun clearFavorites(): Int

}