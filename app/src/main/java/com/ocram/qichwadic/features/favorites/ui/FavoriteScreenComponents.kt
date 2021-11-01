package com.ocram.qichwadic.features.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.common.DefinitionCard
import com.ocram.qichwadic.core.ui.common.SimpleGridView
import com.ocram.qichwadic.core.ui.theme.textStyleSmall

@Composable
fun EmptyFavoritesView() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.favorite_empty))
        Icon(
            modifier = Modifier.padding(vertical = 8.dp),
            painter = painterResource(id = R.drawable.ic_sentiment_dissatisfied),
            contentDescription = ""
        )
    }
}

@Composable
fun FavoritesGrid(
    favorites: List<DefinitionModel>,
    share: (favorite: DefinitionModel) -> Unit,
    deleteOne: (favorite: DefinitionModel) -> Unit
) {
    SimpleGridView(
        cols = 2,
        items = favorites
    ) { favorite, modifier -> FavoriteCard(
        modifier = modifier,
        definition = favorite,
        share = { share(favorite) },
        delete = { deleteOne(favorite) })
    }
}

@Composable
fun FavoriteCard(
    modifier: Modifier,
    definition: DefinitionModel,
    share: () -> Unit,
    delete: () -> Unit
) {
    Surface(
        modifier = modifier
            .background(color = Color.White)
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Column(
            Modifier.padding(all = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DefinitionCard(definition = definition)
            Text(
                definition.dictionaryName ?: "",
                style = textStyleSmall,
                textAlign = TextAlign.Center
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = share) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "")
                }
                IconButton(onClick = delete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewFavoritesGrid() {
    val exampleText = "Example text. <strong>Strong text</strong>"
    FavoritesGrid(
        favorites = listOf(
            DefinitionModel(
                word = "Word",
                meaning = exampleText
            ),
            DefinitionModel(
                word = "Word 2",
                meaning = exampleText.repeat(10)
            ),
            DefinitionModel(
                word = "Word 3",
                meaning = exampleText
            )
        ),
        {},
        {}
    )
}

@Composable
@Preview
fun PreviewEmptyFavoritesView() {
    EmptyFavoritesView()
}