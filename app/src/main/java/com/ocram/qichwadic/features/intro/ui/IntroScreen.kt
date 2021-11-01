package com.ocram.qichwadic.features.intro.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.ocram.qichwadic.core.ui.theme.*

private val DEFAULT_COLOR = Color.White

private val DEFAULT_TEXT_STYLE = textStyleNormal.copy(
    color = DEFAULT_COLOR,
    textAlign = TextAlign.Center
)

private data class IntroScreenItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val imgId: Int
    )

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
fun IntroScreen(finishIntro: () -> Unit) {
    var currentScreenIndex by remember { mutableStateOf(0) }
    val changeScreen = { value: Int ->
        val newVal = currentScreenIndex + value
        if (newVal >= 0 && newVal < screenItems.size) {
            currentScreenIndex = newVal
        }
    }
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
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.weight(1f)){
                Crossfade(targetState = currentScreenIndex) {
                    IntroView(item = screenItems[it])
                }
            }
            Divider(color = Color.LightGray, thickness = 0.5.dp)
            IntroBottomBar(
                currentScreenIndex = currentScreenIndex,
                finishIntro = finishIntro,
                changeScreen = changeScreen
            )
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
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
        Text(
            stringResource(item.description),
            style = DEFAULT_TEXT_STYLE
        )
    }
}

@Composable
fun IntroBottomBar(
    currentScreenIndex: Int,
    changeScreen: (value: Int) -> Unit,
    finishIntro: () -> Unit
) {
    val beforeLastItem = currentScreenIndex < screenItems.lastIndex
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            modifier = Modifier.weight(2f),
            onClick = { finishIntro() }
        ) {
            val textId = if (beforeLastItem) R.string.intro_skip else R.string.intro_done
            Text(
                stringResource(textId).uppercase(),
                color = DEFAULT_COLOR,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Box(Modifier.weight(5f), contentAlignment = Alignment.Center) {
            IntroScreenIndicator(screenItems.size, currentScreenIndex)
        }
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = Modifier.alpha(if (currentScreenIndex > 0) 1f else 0f),
                onClick = { changeScreen(-1) }
            ) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "", tint = DEFAULT_COLOR)
            }
            if (beforeLastItem) {
                IconButton(onClick = { changeScreen(1) }) {
                    Icon(
                        Icons.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        tint = DEFAULT_COLOR
                    )
                }
            }

        }
    }
}

@Composable
fun IntroScreenIndicator(totalCount: Int, current: Int, ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for(i in 0 until totalCount) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(
                        color = if (i == current) Color.White else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    IntroScreen {}
}

@Preview
@Composable
fun PreviewIntroScreenIndicator() {
    IntroScreenIndicator(3, 1)
}