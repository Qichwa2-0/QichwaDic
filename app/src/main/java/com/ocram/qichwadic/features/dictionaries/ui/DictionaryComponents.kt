package com.ocram.qichwadic.features.dictionaries.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import com.ocram.qichwadic.core.ui.common.SelectionDialogText
import com.ocram.qichwadic.core.ui.common.SelectionListDialog
import com.ocram.qichwadic.core.ui.theme.defaultDropdownItemTextStyle

@Composable
fun DictionaryDropDown(
    items: List<DictionaryLanguage>,
    onItemSelected: (language: String) -> Unit,
    selectedLanguage: String
) {
    var expanded by remember { mutableStateOf(false) }
    val canChangeSelection = items.size > 1
    SelectionDialogText(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(5.dp)),
        onClick = { expanded = !expanded },
        text = items.find { it.code == selectedLanguage }?.text ?: "",
        enableSelection = canChangeSelection
    )
    if (canChangeSelection) {
        SelectionListDialog(
            open = expanded,
            items = items,
            onItemSelected = {
                onItemSelected(it.code)
                expanded = !expanded
            },
            onDismissRequest = { expanded = false },
            itemView = {
                Text(
                    text = it.text,
                    modifier = Modifier.fillMaxWidth(),
                    style = defaultDropdownItemTextStyle
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
            Column(
                modifier = Modifier.weight(8f),
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
                modifier = Modifier.weight(2f),
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
        val icon =
            if (dictionary.existsInLocal) Icon(imageVector = Icons.Filled.Delete, contentDescription = "")
            else  Icon(painter = painterResource(id = R.drawable.ic_action_download), contentDescription = "")
        IconButton(
            onClick = { onDownloadClicked() },
            content = { icon }
        )

    }
}