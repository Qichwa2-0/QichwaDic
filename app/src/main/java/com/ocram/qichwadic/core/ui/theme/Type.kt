package com.ocram.qichwadic.core.ui.theme

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

val defaultDropdownTextStyle = TextStyle(
    color = primaryDarkColor,
    textAlign = TextAlign.Center,
    fontSize = textStyleNormal.fontSize
)

val defaultDropdownItemTextStyle = TextStyle(
    color = customTextColor,
    textAlign = TextAlign.Center,
    fontSize = textStyleNormal.fontSize
)
