package com.ocram.qichwadic.core.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val whiteRoundedBorder = Modifier.border(
    border = BorderStroke(1.dp, color = Color.White),
    shape = RoundedCornerShape(5.dp)
)