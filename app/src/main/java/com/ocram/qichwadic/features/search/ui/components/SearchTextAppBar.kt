package com.ocram.qichwadic.features.search.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R

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
fun SearchOfflineIcon(
    offlineSearch: Boolean,
    onSearchOfflineChanged: (offline: Boolean) -> Unit,
) {
    var offline by remember { mutableStateOf(offlineSearch) }

    IconButton(onClick = {
        offline = !offline
        onSearchOfflineChanged(offline)
    }) {
        Crossfade(targetState = offline) { offline ->
            if (offline) {
                Icon(
                    painterResource(R.drawable.ic_baseline_wifi_off_24),
                    tint = Color.Red,
                    contentDescription = ""
                )
            } else {
                Icon(
                    painterResource(R.drawable.ic_baseline_wifi_24),
                    tint = Color.White,
                    contentDescription = ""
                )
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
