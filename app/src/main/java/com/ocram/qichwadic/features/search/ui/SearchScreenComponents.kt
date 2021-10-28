package com.ocram.qichwadic.features.search.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.domain.model.SearchResultModel
import com.ocram.qichwadic.core.ui.DictLang
import com.ocram.qichwadic.core.ui.common.*
import com.ocram.qichwadic.core.ui.theme.*

data class DropdownItem<T>(val text: String, val id: T)

@Composable
fun TextSearchBar(
    openDrawer: () -> Unit,
    placeholder: String,
    searchText: String,
    offlineSearch: Boolean,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    search: () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.primary,
        elevation = 8.dp
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = openDrawer) {
                Icon(Icons.Filled.Menu, contentDescription = "")
            }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Unspecified,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.White
                ),
                singleLine = true,
                value = searchText,
                placeholder = { Text(placeholder, color = Color.White.copy(0.6f)) },
                onValueChange = { onTextChange(it) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            tint = Color.White,
                            contentDescription = "",
                            modifier = Modifier.clickable { search() }
                        )
                    } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        search()
                    }
                ),
            )
            SearchOfflineIcon(
                offlineSearch = offlineSearch,
                onSearchOfflineChanged = onSearchOfflineChanged
            )
        }
    }
}

@Composable
fun SearchOptionsBar(
    selectedItemPos: Int,
    selectedLanguage: String,
    fromQuechua: Boolean,
    onSearchTypeSelected: (pos: Int) -> Unit,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colors.primary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguagesSwitcherView(
            Modifier.weight(41f),
            dictLanguages = stringArrayResource(id = R.array.dictLangs),
            selectedLanguage = selectedLanguage,
            fromQuechua = fromQuechua,
            onLanguageSelected = onLanguageSelected,
            switchFromQuechua = switchFromQuechua
        )
        Spacer(modifier = Modifier.weight(3f))
        SearchTypesDropdown(
            Modifier.weight(41f),
            stringArrayResource(id = R.array.searchTypes),
            selectedItemPos,
            onSearchTypeSelected,
        )
    }
}

@Composable
fun SearchOfflineIcon(
    offlineSearch: Boolean,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
) {
    var offline by remember { mutableStateOf(offlineSearch) }
    val searchModeIcon =
        if (offline) R.drawable.ic_baseline_wifi_off_24
        else R.drawable.ic_baseline_wifi_24
    val tint = if (offline) Color.Red else Color.White

    IconButton(onClick = {
        offline = !offline
        onSearchOfflineChanged(offline)
    }) {
        Icon(
            painterResource(searchModeIcon),
            tint = tint,
            contentDescription = ""
        )
    }
}

@Composable
fun SearchTypesDropdown(
    modifier: Modifier,
    searchTypes: Array<String>,
    selectedItemPos: Int,
    onSearchTypeSelected: (pos: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = searchTypes.mapIndexed { index, s -> DropdownItem(s, index) }

    Box(modifier.then(whiteRoundedBorder)) {
        SelectionDialogText(
            onClick = { expanded = !expanded },
            text = items[selectedItemPos].text,
            textStyle = defaultDropdownTextStyle.copy(color = Color.White),
        )
        SelectionListDialog(
            open = expanded,
            items = items,
            onItemSelected = {
                onSearchTypeSelected(it.id)
            },
            onDismissRequest = { expanded = false },
            itemView = { BasicDropdownItemView(it.text) }
        )
    }

}

@Composable
fun LanguagesSwitcherView(
    modifier: Modifier,
    dictLanguages: Array<String>,
    selectedLanguage: String,
    fromQuechua: Boolean,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit
) {
    Row(
        modifier = modifier.then(whiteRoundedBorder),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val text = @Composable {
            Text(
                text = "QU",
                Modifier.weight(2f),
                textAlign = TextAlign.Center,
                color = accentColor
            )
        }
        val image = @Composable {
            Image(
                modifier = Modifier
                    .weight(1f)
                    .clickable { switchFromQuechua() },
                painter = painterResource(id = R.drawable.ic_swap_horiz_white_24dp),
                contentDescription = "",
            )
        }
        val dictionaryLanguagesDropdown = @Composable{
            DictionaryLanguagesDropdown(
                modifier = Modifier.weight(2f),
                dictLanguages = dictLanguages.map { DictLang(it) },
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
        var components = listOf(text, image, dictionaryLanguagesDropdown)
        if (!fromQuechua) {
            components = components.reversed()
        }
        components.forEach{ it() }
    }
}

@Composable
fun DictionaryLanguagesDropdown(
    modifier: Modifier,
    dictLanguages: List<DictLang>,
    selectedLanguage: String,
    onLanguageSelected: (lang: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    SelectionDialogText(
        modifier = modifier,
        onClick = { expanded = !expanded },
        text = dictLanguages.find{ it.code == selectedLanguage }?.code?.uppercase() ?: "",
        textStyle = defaultDropdownTextStyle.copy(color = Color.White),
    )
    SelectionListDialog(
        open = expanded,
        items = dictLanguages,
        onItemSelected = {
            onLanguageSelected(it.code)
        },
        onDismissRequest = { expanded = false },
        itemView = { BasicDropdownItemView(it.name) }
    )
}

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
fun PreviewTextSearchBar() {
    TextSearchBar(
        openDrawer = {},
        placeholder = "Hola",
        searchText = "abc",
        offlineSearch = false,
        {},
        onTextChange = {}
    ) {}
}

@Composable
@Preview
fun PreviewSearchOptionsBar() {
    SearchOptionsBar(
        selectedItemPos = 0,
        selectedLanguage = "es",
        fromQuechua = true,

        onSearchTypeSelected = {},
        {}
    ) {}
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