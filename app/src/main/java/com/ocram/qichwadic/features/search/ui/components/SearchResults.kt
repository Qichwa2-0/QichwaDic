package com.ocram.qichwadic.features.search.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.ui.common.*
import com.ocram.qichwadic.core.ui.theme.*

@Composable
fun SearchResultsDropdown(
    searchResults: List<SearchResultModel>,
    selectedItemPos: Int,
    onItemSelected: (pos: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = searchResults[selectedItemPos]
    val buildItemText = { item: SearchResultModel -> "(${item.total}) ${item.dictionaryName}" }
    if(searchResults.isNotEmpty()) {
        val canChangeSelection = searchResults.size > 1
        SelectionDialogText(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(5.dp)),
            onClick = { expanded = !expanded },
            text = buildItemText(selectedItem),
            enableSelection = canChangeSelection
        )
        SelectionListDialog(
            open = expanded,
            items = searchResults,
            onItemSelected = {
                onItemSelected(searchResults.indexOf(it))
                expanded = !expanded
            },
            onDismissRequest = { expanded = false },
            titleView = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(primaryDarkColor)
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Results found in ${searchResults.size} dictionaries",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            },
            itemView = { DictionaryNameWithTotal(it) }
        )
    }
}

@Composable
fun DictionaryNameWithTotal(item: SearchResultModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Surface(
            modifier = Modifier.weight(2f),
            color = primaryColor,
            border = BorderStroke(1.dp, primaryColor),
            shape = CircleShape
        ) {
            Text(
                text = "${item.total}",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
        Text(
            modifier = Modifier.weight(9f).padding(horizontal = 8.dp),
            text = item.dictionaryName ?: "",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SearchResultsError(modifier: Modifier = Modifier, offline: Boolean) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(if (offline) R.string.error_offline else R.string.error_online),
            textAlign = TextAlign.Center
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_sentiment_dissatisfied),
            contentDescription = ""
        )
    }
}

@Composable
fun SearchNoResultsOnline(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sentiment_dissatisfied),
            contentDescription = ""
        )
        Text(stringResource(id = R.string.no_results_online))
    }
}

@Composable
fun SearchNoResultsOffline(modifier: Modifier = Modifier, goToDictionaries: () -> Unit) {
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(id = R.string.no_results_offline),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = customTextColor
        )
        TextButton(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = goToDictionaries,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.get_dictionaries),
                    fontSize = 14.sp,
                )
                Icon(
                    painterResource(id = R.drawable.ic_action_book_holo_dark),
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
fun ResultDefinitionCard(
    modifier: Modifier = Modifier,
    definition: DefinitionModel,
    shareDefinition: () -> Unit,
    saveFavorite: () -> Unit
) {
    Surface(
        modifier = modifier
            .background(color = Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefinitionCard(definition = definition, modifier = Modifier.weight(8f))
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = saveFavorite) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "")
                }
                IconButton(onClick = shareDefinition) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewSearchResultsError() {
    SearchResultsError(offline = true)
}

@Composable
@Preview
fun PreviewSearchNoResultsOnline() {
    SearchNoResultsOnline()
}

@Composable
@Preview
fun PreviewSearchNoResultsOffline() {
    SearchNoResultsOffline {}
}