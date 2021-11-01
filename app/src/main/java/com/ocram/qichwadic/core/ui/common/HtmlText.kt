package com.ocram.qichwadic.core.ui.common

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

fun parseHtml(text: String): Spanned {
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

@Composable
fun HtmlText(
    text: String,
    removeLinksUnderline: Boolean = false,
    alignCenter: Boolean = true,
    textSize: Float? = 14f
) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(parseHtml(text))
            textSize?.let { setTextSize(it) }
            movementMethod = LinkMovementMethod.getInstance()
            if(alignCenter) {
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            if(removeLinksUnderline) {
                removeLinksUnderline()
            }
        }
    })
}

private fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(u.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
    }
    text = spannable
}