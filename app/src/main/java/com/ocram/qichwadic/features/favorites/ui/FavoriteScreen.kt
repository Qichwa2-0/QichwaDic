package com.ocram.qichwadic.features.favorites.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.common.ConfirmDialog
import com.ocram.qichwadic.core.ui.common.TopBar
import com.ocram.qichwadic.core.util.parseHtml

@Composable
fun FavoriteScreen(
    deletedFavoriteState: DeletedFavoriteState,
    favorites: List<DefinitionModel>,
    onBackPressed: () -> Unit,
    showSnackbar: (message: String, onDismiss: () -> Unit) -> Unit,
    deleteAll: () -> Unit,
    share: (text: String) ->Unit,
    deleteOne: (favorite: DefinitionModel) -> Unit,
    resetDeleteStates: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    ConfirmDialog(
        open = showDeleteAllDialog,
        dismiss = { showDeleteAllDialog = false },
        title = stringResource(R.string.favorite_dialog_title),
        content = stringResource(R.string.favorite_dialog_content),
        dismissBtnText = stringResource(R.string.favorite_dialog_clear_cancel),
        confirmBtnText = stringResource(R.string.favorite_dialog_clear_confirm)
    ) {
        deleteAll()
    }

    if (deletedFavoriteState != DeletedFavoriteState.NONE) {
        deletedFavoriteState.msgId?.let {
            val text  = stringResource(it)
            DisposableEffect(deletedFavoriteState) {
                showSnackbar(text, resetDeleteStates)
                onDispose {
                    resetDeleteStates()
                }
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopBar(
            title = stringResource(R.string.nav_favorites),
            onButtonClicked = onBackPressed,
            actions = {
                IconButton(
                    onClick = {
                        if (favorites.isNotEmpty()) {
                            showDeleteAllDialog = true
                        }
                    },
                    enabled = favorites.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = ""
                    )
                }
            }
        )
        if(favorites.isEmpty()) {
            EmptyFavoritesView()
        } else {
            FavoritesGrid(
                favorites = favorites,
                share = {
                    val textToShare = context.getString(
                        R.string.share_definition_from_dictionary,
                        it.dictionaryName,
                        it.word,
                        it.meaning
                    )
                    share(parseHtml(textToShare).toString())
                },
                deleteOne = { deleteOne(it) }
            ) 
        }
    }
}

