package com.ocram.qichwadic.features.search.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun SearchResultsDropdown(
    searchResults: List<SearchResultModel>,
    currentResult: SearchResultModel?,
    onItemSelected: (pos: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    if(searchResults.isNotEmpty()) {
        val canChangeSelection = searchResults.size > 1
        Box(
            Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .border(1.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(5.dp))) {
            SelectionDialogText(
                onClick = { expanded = !expanded },
                text = currentResult?.titleWithTotal ?: "",
                enableSelection = canChangeSelection
            )
        }

        val dialogTitle = LocalContext
            .current
            .resources
            .getQuantityString(
                R.plurals.results_found_in_dictionaries,
                searchResults.size,
                searchResults.sumOf { it.total },
                searchResults.size
            )
        SelectionListDialog(
            open = expanded,
            items = searchResults,
            onItemSelected = {
                onItemSelected(it)
                expanded = !expanded
            },
            onDismissRequest = { expanded = false },
            titleView = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primaryVariant)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = dialogTitle,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onPrimary
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
        Text(
            modifier = Modifier
                .weight(9f)
                .padding(horizontal = 8.dp),
            text = item.dictionaryName ?: "",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.weight(2f),
        ) {
            Text(
                text = "${item.total}",
                color = MaterialTheme.colors.secondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
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
            modifier = Modifier.padding(horizontal = 4.dp),
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
            color = MaterialTheme.colors.secondaryVariant
        )
        TextButton(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = goToDictionaries,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colors.onPrimary,
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
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = 2.dp,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefinitionCard(definition = definition, modifier = Modifier.weight(8f))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = saveFavorite) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "")
                }
                IconButton(onClick = shareDefinition) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "")
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

@Composable
@Preview
fun PreviewResultDefinitionCard() {
    ResultDefinitionCard(
        definition = DefinitionModel(word = "My word", meaning = "A long meaning. More meaning"),
        shareDefinition = { /*TODO*/ }
    ) {}
}