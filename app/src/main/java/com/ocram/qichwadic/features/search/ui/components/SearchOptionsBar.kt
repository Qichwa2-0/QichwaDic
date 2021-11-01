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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.BasicDropdownItemView
import com.ocram.qichwadic.core.ui.common.SelectionDialogText
import com.ocram.qichwadic.core.ui.common.SelectionListDialog
import com.ocram.qichwadic.core.ui.common.whiteRoundedBorder
import com.ocram.qichwadic.core.ui.theme.accentColor
import com.ocram.qichwadic.core.ui.theme.defaultDropdownTextStyle
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
            searchTypes,
            selectedItemPos,
            onSearchTypeSelected,
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
                dictLanguages = dictionaryLanguages,
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
fun SearchTypesDropdown(
    modifier: Modifier,
    searchTypes: List<String>,
    selectedItemPos: Int,
    onSearchTypeSelected: (pos: Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier.then(whiteRoundedBorder)) {
        SelectionDialogText(
            onClick = { expanded = !expanded },
            text = searchTypes[selectedItemPos],
            textStyle = defaultDropdownTextStyle.copy(color = Color.White),
        )
        SelectionListDialog(
            open = expanded,
            items = searchTypes,
            onItemSelected = {
                onSearchTypeSelected(searchTypes.indexOf(it))
            },
            onDismissRequest = { expanded = false },
            itemView = { BasicDropdownItemView(it) }
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