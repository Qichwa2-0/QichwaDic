package com.ocram.qichwadic.features.favorites.ui

import androidx.compose.runtime.getValue
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

sealed class ItemsState {
    object Loading: ItemsState()
    class Result(val items: List<DefinitionModel>): ItemsState()
}

data class State(
    val deletedFavoriteState: DeletedFavoriteState = DeletedFavoriteState.NONE,
    val favorites: LiveData<ItemsState> = MutableLiveData(ItemsState.Loading),
    val fetchingMoreData: Boolean = false,
    val canLoadMoreData: Boolean = true
)

class FavoriteViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    var state by mutableStateOf(State())

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        var liveData: LiveData<ItemsState>

        viewModelScope.launch {
            liveData = Transformations.map(favoriteInteractor.getFavorites(page = 1).asLiveData()) {
                ItemsState.Result(it)
            }
            setState { copy(favorites = liveData) }
        }
    }

    fun fetchMoreFavorites() {
        val currentState = state.favorites.value

        if (!(currentState is ItemsState.Result && state.canLoadMoreData)) return

        val newPage = (currentState.items.size / 20) + 1

        viewModelScope.launch {
            val liveData: LiveData<ItemsState> = Transformations.map(favoriteInteractor.getFavorites(newPage).asLiveData()) {
                setState { copy(canLoadMoreData = it.size == 20) }
                ItemsState.Result(currentState.items + it)
            }
            setState { copy(favorites = liveData) }
        }
    }

    fun removeFavorite(favorite: DefinitionModel) {
        viewModelScope.launch {
            val removed = favoriteInteractor.removeFavorite(favorite)
            setState {
                copy(
                    deletedFavoriteState = if (removed) {
                        DeletedFavoriteState.DELETE_SUCCESS
                    } else {
                        DeletedFavoriteState.DELETE_ERROR
                    }
                )
            }
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            val cleared = favoriteInteractor.clearFavorites()
            setState {
                copy(
                    deletedFavoriteState = if (cleared) {
                        DeletedFavoriteState.CLEAR_SUCCESS
                    } else {
                        DeletedFavoriteState.CLEAR_ERROR
                    }
                )
            }
        }
    }

    fun resetDeleteStates() {
        setState {
            copy(deletedFavoriteState = DeletedFavoriteState.NONE)
        }
    }

    private fun setState(applyFn: State.() -> State) {
        state = state.applyFn()
    }
}
