package com.ocram.qichwadic.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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

private val DarkColors = darkColors()

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme (
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

@Preview
@Composable
fun PreviewQichwaDicLightTheme() {
    AppTheme(darkTheme = false) { SampleApp() }
}

@Preview
@Composable
fun PreviewQichwaDicDarkTheme() {
    AppTheme(darkTheme = true) { SampleApp() }
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