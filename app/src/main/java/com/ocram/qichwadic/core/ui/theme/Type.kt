package com.ocram.qichwadic.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val faqQuestionTextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold
)

val faqQuestionReplyStyle = TextStyle(
    fontSize = 14.sp,
)

val textStyleSmall = TextStyle(
    fontSize = 12.sp
)

val textStyleNormal = TextStyle(
    fontSize = 14.sp
)

@Composable
fun defaultDropdownTextStyle(): TextStyle = TextStyle(
    color = MaterialTheme.colors.primary,
    textAlign = TextAlign.Center,
    fontSize = textStyleNormal.fontSize
)

@Composable
fun searchOptionsDropdownTextStyle(): TextStyle
        = defaultDropdownTextStyle().copy(color = MaterialTheme.colors.onPrimary)

@Composable
fun defaultDropdownItemTextStyle(): TextStyle = TextStyle(
    textAlign = TextAlign.Center,
    fontSize = textStyleNormal.fontSize
)
