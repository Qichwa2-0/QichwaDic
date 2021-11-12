package com.ocram.qichwadic.features.dictionaries.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.ui.view.DictLang
import com.ocram.qichwadic.core.ui.common.SelectionDialogText
import com.ocram.qichwadic.core.ui.common.SelectionListDialog
import com.ocram.qichwadic.core.ui.theme.defaultDropdownItemTextStyle

@Composable
fun DictionaryDropDown(
    items: List<DictLang>,
    onItemSelected: (language: String) -> Unit,
    selectedLanguage: String
) {
    var expanded by remember { mutableStateOf(false) }
    val canChangeSelection = items.size > 1
    Box(
        Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(5.dp))
    ) {
        SelectionDialogText(
            onClick = { expanded = !expanded },
            text = items.find { it.code == selectedLanguage }?.name ?: "",
            enableSelection = canChangeSelection
        )
    }
    if (canChangeSelection) {
        SelectionListDialog(
            open = expanded,
            items = items,
            onItemSelected = {
                onItemSelected(items[it].code)
                expanded = !expanded
            },
            onDismissRequest = { expanded = false },
            itemView = {
                Text(
                    text = it.name,
                    modifier = Modifier.fillMaxWidth(),
                    style = defaultDropdownItemTextStyle()
                )
            }
        )
    }

}

@Composable
fun DictionaryCard(
    modifier: Modifier = Modifier,
    dictionary: DictionaryModel,
    onDownloadClicked: () -> Unit
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(8f).padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dictionary.name ?: "",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Text(
                    text = dictionary.description ?: "",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = dictionary.author ?: "",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(id = R.string.dictionary_totalEntries, dictionary.totalEntries),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DictionaryIndicator(dictionary) { onDownloadClicked() }
            }
        }
    }
}

@Composable
fun DictionaryIndicator(
    dictionary: DictionaryModel,
    onDownloadClicked: () -> Unit
) {
    if(dictionary.downloading) {
        CircularProgressIndicator()
    } else {
        IconButton(
            onClick = { onDownloadClicked() },
            content = {
                if (dictionary.existsInLocal) {
                    Icon(Icons.Filled.Delete, contentDescription = "")
                } else {
                    Icon(painterResource(R.drawable.ic_action_download), contentDescription = "")
                }
            }
        )

    }
}

@Composable
@Preview
fun DictionaryCardPreview() {
    DictionaryCard(
        dictionary = DictionaryModel(
            id = 1,
            name = "Dictionary",
            description = "some description",
            author = "the author",
            totalEntries = 199
        )
    ) {}
}