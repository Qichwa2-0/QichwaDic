package com.ocram.qichwadic.features.favorites.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.ocram.qichwadic.R

import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import kotlinx.coroutines.launch

enum class DeletedFavoriteState(val msgId: Int? = null) {
    NONE,
    DELETE_SUCCESS(R.string.favorite_deleted_success),
    DELETE_ERROR(R.string.favorite_deleted_error),
    CLEAR_SUCCESS(R.string.favorite_cleared_success),
    CLEAR_ERROR(R.string.favorite_cleared_error),
}

class FavoriteViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    var loading by mutableStateOf(true)
        private set

    val favorites = mutableStateListOf<DefinitionModel>()

    var deletedFavoriteState by mutableStateOf(DeletedFavoriteState.NONE)
        private set

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favorites.apply {
                clear()
                addAll(favoriteInteractor.getFavorites())
            }
            loading = false
        }
    }

    fun removeFavorite(favorite: DefinitionModel) {
        viewModelScope.launch {
            val removed = favoriteInteractor.removeFavorite(favorite)
            deletedFavoriteState = if (removed) {
                DeletedFavoriteState.DELETE_SUCCESS
            } else {
                DeletedFavoriteState.DELETE_ERROR
            }
            loadFavorites()
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            val cleared = favoriteInteractor.clearFavorites()
            deletedFavoriteState = if (cleared) {
                DeletedFavoriteState.CLEAR_SUCCESS
            } else {
                DeletedFavoriteState.CLEAR_ERROR
            }
            loadFavorites()
        }
    }

    fun resetDeleteStates() {
        deletedFavoriteState = DeletedFavoriteState.NONE
    }
}
