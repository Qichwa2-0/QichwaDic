package com.ocram.qichwadic.core.ui.theme

import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// basic light theme colors
val primaryColor = Color(0xFF673AB7)
val primaryDarkColor = Color(0xFF320b86)
val primaryLightColor = Color(0xFF9a67ea)
val secondaryColor = Color(0xFF009688)
val secondaryDarkColor = Color(0xFF00675b)
val secondaryLightColor = Color(0xFF52c7b8)
val errorColor = Color.Red
// basic dark theme colors



// additional colors
val dividerColor = Color(0xFFB6B6B6)
val textIconsColor = Color(0xFFFFFFFF)
val listItemHeaderColor = Color(0xFF1d6580)
val showCaseTitleColor = Color(0xFF00BDE9)
val customTextColor = Color(0xFF1c2a47)
val appIntroBackgroundColor = Color(0xFF00BDE9)
val textLinkColor = Color(0xFF3F51B5)

val placeholderColor = Color.White.copy(0.6f)

@Composable
fun searchTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Unspecified,
        focusedIndicatorColor = Color.LightGray,
        unfocusedIndicatorColor = Color.White
    )
}