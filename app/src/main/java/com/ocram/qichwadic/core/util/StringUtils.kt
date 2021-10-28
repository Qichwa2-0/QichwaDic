package com.ocram.qichwadic.core.util

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun parseHtml(text: String): Spanned {
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}