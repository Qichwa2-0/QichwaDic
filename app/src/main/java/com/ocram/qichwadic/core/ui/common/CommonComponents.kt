package com.ocram.qichwadic.core.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.theme.defaultDropdownItemTextStyle
import com.ocram.qichwadic.core.ui.theme.defaultDropdownTextStyle
import com.ocram.qichwadic.core.ui.theme.textStyleNormal
import com.ocram.qichwadic.core.ui.theme.textStyleSmall
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun AppLogo(modifier: Modifier = Modifier, width: Float = 320F) {
    Box(modifier.width(width.dp)) {
        Image(
            modifier = Modifier.padding(8.dp),
            painter = painterResource(id = R.drawable.logo_q),
            contentDescription = "App Icon",
        )
    }
}

@Composable
fun TopBar(
    title: String = stringResource(id = R.string.app_name),
    buttonIcon: ImageVector = Icons.Filled.ArrowBack,
    onButtonClicked: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar (
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onButtonClicked) {
                Icon(buttonIcon, contentDescription = "")
            }
        },
        actions = { actions() }
    )
}

@Composable
fun LinearLoadingIndicator(
    modifier: Modifier = Modifier,
    @StringRes loadingMessageId: Int = R.string.loading
) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(loadingMessageId),
            Modifier.padding(vertical = 16.dp)
        )
        LinearProgressIndicator()
    }

}

@Composable
fun DefinitionCard(
    definition: DefinitionModel,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = definition.word?.uppercase() ?: "",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = textStyleNormal
            )
            HtmlText(
                text = definition.meaning ?: "",
                textSize = textStyleSmall.fontSize.value
            )
        }
    }

}

@Composable
fun SelectionDialogText (
    onClick: () -> Unit,
    text: String,
    textStyle: TextStyle = defaultDropdownTextStyle(),
    enableSelection: Boolean = true
) {
    val clickableModifier = if (enableSelection) Modifier.clickable { onClick() } else Modifier
    Row(
        Modifier
            .fillMaxWidth()
            .then(clickableModifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = text,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(1f),
            style = textStyle
        )
        if (enableSelection) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "",
                tint = textStyle.color
            )
        }
    }
}

@Composable
fun BasicDropdownItemView(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = defaultDropdownItemTextStyle()
    )
}

@Composable
fun ReversibleList(reversed: Boolean = false, composables: List<@Composable () -> Unit>) {
    val components = if (reversed) composables.reversed() else composables
    components.forEach{ it() }
}

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 2,
    onLoadMore: () -> Unit
) {
    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }
    LaunchedEffect(loadMore) {
        println("loadMore $loadMore")
        if (loadMore) {
            onLoadMore()
        }
    }
}

@Composable
@Preview
fun PreviewLogo() {
    AppLogo()
}

@Composable
@Preview
fun PreviewDefinitionCard() {
    DefinitionCard(
        definition = DefinitionModel(
            word = "DEFINITION",
            meaning = "Text for meaning. <strong>Strong text</strong>"
        ),
    )
}

@Composable
@Preview
fun PreviewSelectionDialogText() {
    SelectionDialogText(onClick = {}, text = "Text")
}

