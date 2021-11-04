package com.ocram.qichwadic.features.search.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.*
import com.ocram.qichwadic.core.ui.theme.searchOptionsDropdownTextStyle
import com.ocram.qichwadic.core.ui.view.DictLang

@Composable
fun SearchOptionsBar(
    selectedItemPos: Int,
    selectedLanguage: String,
    fromQuechua: Boolean,
    onSearchTypeSelected: (pos: Int) -> Unit,
    onLanguageSelected: (lang: String) -> Unit,
    switchFromQuechua: () -> Unit,
) {
    val searchTypes = stringArrayResource(id = R.array.searchTypes).asList()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colors.primary)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguagesSwitcherView(
            Modifier.weight(49f),
            dictLanguages = stringArrayResource(id = R.array.dictLangs),
            selectedLanguage = selectedLanguage,
            fromQuechua = fromQuechua,
            onLanguageSelected = onLanguageSelected,
            switchFromQuechua = switchFromQuechua
        )
        Spacer(modifier = Modifier.weight(2f))
        OptionsBarDropdown(
            modifier = Modifier
                .weight(49f)
                .then(whiteRoundedBorder),
            items = searchTypes,
            selectedPos = selectedItemPos,
            buildSelectedItemText = { it },
            buildTextForItemInList = { it },
            onItemSelected = onSearchTypeSelected,
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
    val dictionaryLanguages = remember {
        dictLanguages.map { DictLang(it) }.filterNot { it.isQuechua }
    }
    val dictionaryLangCodes = remember { dictionaryLanguages.map(DictLang::code) }
    Row(
        modifier = modifier.then(whiteRoundedBorder),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReversibleList(
            reversed = !fromQuechua,
            composables = listOf(
                {
                    Text(
                        text = "QU",
                        Modifier.weight(2f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.secondary
                    )
                },
                {
                    Image(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { switchFromQuechua() },
                        painter = painterResource(id = R.drawable.ic_swap_horiz_white_24dp),
                        contentDescription = "",
                    )
                },
                {
                    OptionsBarDropdown(
                        modifier = Modifier.weight(2f),
                        items = dictionaryLanguages,
                        selectedPos = dictionaryLangCodes.indexOf(selectedLanguage),
                        buildSelectedItemText = { it.code.uppercase() },
                        buildTextForItemInList = { it.name } ,
                        onItemSelected = { onLanguageSelected(dictionaryLanguages[it].code) }
                    )
                }
            )
        )
    }
}

@Composable
fun <T> OptionsBarDropdown(
    modifier: Modifier,
    items: List<T>,
    selectedPos: Int,
    buildSelectedItemText: (item: T) -> String,
    buildTextForItemInList: (item: T) -> String,
    onItemSelected: (pos: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        SelectionDialogText(
            onClick = { expanded = !expanded },
            text = buildSelectedItemText(items[selectedPos]),
            textStyle = searchOptionsDropdownTextStyle(),
        )
        SelectionListDialog(
            open = expanded,
            items = items,
            onItemSelected = { onItemSelected(it) },
            onDismissRequest = { expanded = false },
            itemView = { BasicDropdownItemView(buildTextForItemInList(it)) }
        )
    }
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