package com.ocram.qichwadic.features.favorites.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.common.ConfirmDialog
import com.ocram.qichwadic.core.ui.common.InfiniteListHandler
import com.ocram.qichwadic.core.ui.common.LinearLoadingIndicator
import com.ocram.qichwadic.core.ui.common.TopBar
import org.koin.androidx.compose.getViewModel

@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = getViewModel(),
    onBackPressed: () -> Unit,
    showSnackbar: (message: String, onDismiss: () -> Unit) -> Unit,
    share: (text: String) ->Unit,
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    ConfirmDialog(
        open = showDeleteAllDialog,
        dismiss = { showDeleteAllDialog = false },
        title = stringResource(R.string.favorite_dialog_title),
        content = stringResource(R.string.favorite_dialog_content),
        dismissBtnText = stringResource(R.string.favorite_dialog_clear_cancel),
        confirmBtnText = stringResource(R.string.favorite_dialog_clear_confirm),
        onConfirm = viewModel::clearFavorites
    )
    val uiState = viewModel.state
    if (uiState.deletedFavoriteState != DeletedFavoriteState.NONE) {
        uiState.deletedFavoriteState.msgId?.let {
            val text  = stringResource(it)
            DisposableEffect(uiState.deletedFavoriteState) {
                showSnackbar(text, viewModel::resetDeleteStates)
                onDispose {
                    viewModel.resetDeleteStates()
                }
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        val state by uiState.favorites.observeAsState(ItemsState.Loading)
        when (state) {
            is ItemsState.Result -> {
                FavoriteTopBar(true, onBackPressed = onBackPressed) {
                    showDeleteAllDialog = true
                }
                FavoriteView(
                    favorites = (state as ItemsState.Result).items,
                    share = share,
                    removeOne = viewModel::removeFavorite,
                    canLoadMoreData = uiState.canLoadMoreData,
                    fetchMoreItems = viewModel::fetchMoreFavorites
                )
            }
            else -> {
                FavoriteTopBar(false, onBackPressed = onBackPressed)
                LinearLoadingIndicator()
            }
        }
    }
}

@Composable
fun FavoriteTopBar(
    enableClearAll: Boolean,
    onBackPressed: () -> Unit,
    onClearPressed: () -> Unit = {}
) {
    TopBar(
        title = stringResource(R.string.nav_favorites),
        onButtonClicked = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    if (enableClearAll) {
                        onClearPressed()
                    }
                },
                enabled = enableClearAll
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
fun FavoriteView(
    favorites: List<DefinitionModel>,
    share: (text: String) -> Unit,
    removeOne: (favorite: DefinitionModel) -> Unit,
    canLoadMoreData: Boolean,
    fetchMoreItems: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    Surface(Modifier.fillMaxSize()) {
        if(favorites.isEmpty()) {
            EmptyFavoritesView()
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(items = favorites, key = { item -> item.id }) { favorite ->
                        FavoriteCard(
                            modifier = Modifier.fillMaxWidth(),
                            definition = favorite,
                            share = {
                                share(
                                    context.getString(
                                        R.string.share_definition_from_dictionary,
                                        favorite.dictionaryName,
                                        favorite.word,
                                        favorite.meaning
                                    )
                                )
                            },
                            delete = { removeOne(favorite) }
                        )
                    }
                }
                if (canLoadMoreData) {
                    InfiniteListHandler(listState = listState) {
                        fetchMoreItems()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewFavoriteTopBar() {
    FavoriteTopBar(enableClearAll = true, onBackPressed = {})
}

@Preview
@Composable
fun PreviewEmptyFavoriteView() {
    FavoriteView(favorites = emptyList(), share = {}, removeOne = {}, canLoadMoreData = false) {}
}

@Preview
@Composable
fun PreviewFavoriteView() {
    val items = listOf(
        DefinitionModel(id = 1, word = "Word 1", meaning = "Meaning for 1"),
        DefinitionModel(id = 2, word = "Word 2", meaning = "Meaning for 2"),
        DefinitionModel(id = 3, word = "Word 3", meaning = "Meaning for 3")
    )
    FavoriteView(favorites = items, share = {}, removeOne = {}, canLoadMoreData = false) {}
}