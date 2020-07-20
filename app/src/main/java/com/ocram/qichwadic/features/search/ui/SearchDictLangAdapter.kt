package com.ocram.qichwadic.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.DictLang

class SearchDictLangAdapter(context: Context) : ArrayAdapter<DictLang>(context, R.layout.item_spinner_dict_lang) {

    private val dictLangs: List<DictLang>

    init {
        val codeTextLangs = context.resources.getStringArray(R.array.dictLangs)
        this.dictLangs = codeTextLangs.map { DictLang(it) }.filter { !it.isQuechua() }.map { it }
    }

    override fun getCount(): Int {
        return this.dictLangs.size
    }

    fun getPosByValue(langCode: String): Int {
        for (i in this.dictLangs.indices) {
            val dictLang = this.dictLangs[i]
            if (dictLang.code.equals(langCode, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    override fun getItem(position: Int): DictLang? {
        return dictLangs[position]
    }

    @SuppressLint("DefaultLocale")
    private fun getCustomView(position: Int, parent: ViewGroup, dropdown: Boolean): View {
        val inflater = LayoutInflater.from(parent.context)

        val layoutId = if (dropdown) R.layout.item_spinner_dropdown_search_type else R.layout.item_spinner_dict_lang
        val layout = inflater.inflate(layoutId, parent, false)

        val dictLang = getItem(position)
        val tvName = layout.findViewById<TextView>(R.id.tvName)
        if (dictLang != null) {
            val displayText = if (dropdown) dictLang.name else dictLang.code.toUpperCase()
            tvName.text = displayText
        }
        return layout
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent, true)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent, false)
    }
}
