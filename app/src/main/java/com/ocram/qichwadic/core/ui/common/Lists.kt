package com.ocram.qichwadic.core.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun <T>LazySelectableList(
    items: List<T>,
    onItemSelected: (item: Int) -> Unit,
    itemView: (@Composable (item: T) -> Unit)
) {
    Surface(shape = RoundedCornerShape(10.dp),) {
        LazyColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = items,
                key = { index, _ -> index }
            ) { index, item ->
                Box(
                    Modifier
                        .clickable { onItemSelected(index) }
                        .padding(vertical = 8.dp)
                ) {
                    itemView(item)
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewLazySelectableList() {
    LazySelectableList(
        items = listOf("Kotlin", "Java", "Python"),
        onItemSelected = {},
        itemView = {
            Text("Hello")
        }
    )
}