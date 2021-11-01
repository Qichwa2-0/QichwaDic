package com.ocram.qichwadic.features.intro.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.theme.textStyleNormal

private val DEFAULT_COLOR = Color.White

private val DEFAULT_TEXT_STYLE = textStyleNormal.copy(
    color = DEFAULT_COLOR,
    textAlign = TextAlign.Center
)

private data class IntroScreenItem(val title: Int, val description: Int, @DrawableRes val imgId: Int)

private val screenItems = listOf(
    IntroScreenItem(
        R.string.intro_multisearch_title,
        R.string.intro_multisearch_description,
        R.drawable.search
    ),
    IntroScreenItem(
        R.string.intro_searchcriteria_title,
        R.string.intro_searchcriteria_description,
        R.drawable.searchtype2
    ),
    IntroScreenItem(
        R.string.intro_offline_title,
        R.string.intro_offline_description,
        R.drawable.dictionaries2
    )
)

@Composable
fun IntroScreen(finish: () -> Unit) {
    var screenState by remember { mutableStateOf(0) }
    val beforeLastItem = screenState < screenItems.lastIndex
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        colorResource(id = R.color.primary_color),
                        colorResource(id = R.color.qichwa_showcase_title)
                    ),
                )
            )
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)){
                Crossfade(targetState = screenState) {
                    IntroView(item = screenItems[it])
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { finish() }) {
                    Text(
                        stringResource(id = R.string.intro_skip).uppercase(),
                        color = DEFAULT_COLOR,
                        modifier = Modifier.alpha(if (beforeLastItem) 1f else 0f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("- - -", color = DEFAULT_COLOR)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(screenState > 0) {
                        IconButton(onClick = { screenState -= 1 }) {
                            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "", tint = DEFAULT_COLOR)
                        }
                    }
                    if (beforeLastItem) {
                        IconButton(onClick = { screenState += 1 }) {
                            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "", tint = DEFAULT_COLOR)
                        }
                    } else {
                        TextButton(onClick = { finish() }) {
                            Text(
                                stringResource(id = R.string.intro_done).uppercase(),
                                color = DEFAULT_COLOR,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun IntroView(item: IntroScreenItem) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(stringResource(item.title), style = DEFAULT_TEXT_STYLE.copy(fontSize = 16.sp))
        Image(
            painterResource(item.imgId),
            modifier = Modifier.weight(1f).padding(16.dp),
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
        Text(
            stringResource(item.description),
            style = DEFAULT_TEXT_STYLE
        )
    }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    IntroScreen {}
}