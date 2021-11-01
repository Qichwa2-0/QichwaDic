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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T>InfiniteScrollList(
    modifier: Modifier = Modifier,
    items: List<T>,
    computeKey: (item: T) -> Any,
    state: LazyListState,
    fetchMoreLoading: Boolean,
    itemView: @Composable (item: T) -> Unit
) {
    LazyColumn(
        state = state,
        modifier = modifier
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> computeKey(item) }
        ) { index, item ->
            itemView(item)
            if(index == items.size - 1 && fetchMoreLoading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
internal fun <T>LazySelectableList(
    items: List<T>,
    onItemSelected: (item: T) -> Unit,
    itemView: (@Composable (item: T) -> Unit)
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = items,
                key = { index, _ -> index }
            ) { _, item ->
                Box(
                    Modifier
                        .clickable { onItemSelected(item) }
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
fun PreviewInfiniteScrollList() {
    InfiniteScrollList(
        items = listOf("A", "B", "C"),
        computeKey = { it },
        state = rememberLazyListState(),
        fetchMoreLoading = false
    ) {
        Text(it)
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