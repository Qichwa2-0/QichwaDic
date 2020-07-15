package com.ocram.qichwadic.features.favorites.ui

import androidx.lifecycle.*

import com.ocram.qichwadic.features.common.domain.DefinitionModel
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    val favorites = favoriteInteractor.getFavorites()

    val deleteFavoriteResult = MutableLiveData<Boolean>()

    val clearFavoriteResult = MutableLiveData<Boolean>()

    fun removeFavorite(favorite: DefinitionModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val removed = favoriteInteractor.removeFavorite(favorite)
                if (removed)  deleteFavoriteResult.postValue(removed)
                else deleteFavoriteResult.postValue(false)
            }
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val cleared = favoriteInteractor.clearFavorites()
                if (cleared) clearFavoriteResult.postValue(cleared)
                else clearFavoriteResult.postValue(false)
            }
        }
    }
}
