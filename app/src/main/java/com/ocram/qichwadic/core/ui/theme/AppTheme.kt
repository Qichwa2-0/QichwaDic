package com.ocram.qichwadic.core.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ocram.qichwadic.core.ui.common.TopBar

private val LightColors = lightColors(
    primary = primaryColor,
    primaryVariant = primaryDarkColor,
    secondary = secondaryColor,
    secondaryVariant = secondaryDarkColor,
    error = errorColor
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme (
        colors = LightColors,
        content = content
    )
}

@Preview
@Composable
private fun PreviewQichwaDicLightTheme() {
    AppTheme { SampleApp() }
}

@Composable
private fun SampleApp() {
    Scaffold {
        Column {
            TopBar(
                title = "My App",
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClicked = {}
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Title", style = MaterialTheme.typography.h2, textAlign = TextAlign.Center)
            }
        }
    }
}