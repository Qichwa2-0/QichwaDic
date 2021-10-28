package com.ocram.qichwadic.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightThemesColors = lightColors(
    primary = primaryColor,
    primaryVariant = primaryDarkColor,
    secondary = accentColor
)

@Composable
fun QichwaDicTheme(content: @Composable () -> Unit) {
    MaterialTheme (
        colors = LightThemesColors,
        content = content
    )
}