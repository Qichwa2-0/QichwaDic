package com.ocram.qichwadic.features.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.DictLang
import com.ocram.qichwadic.databinding.ItemSpinnerDictLangBinding
import com.ocram.qichwadic.databinding.ItemSpinnerDropdownSearchTypeBinding
import java.util.*

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

    override fun getItem(position: Int): DictLang {
        return dictLangs[position]
    }

    @SuppressLint("DefaultLocale")
    private fun getCustomView(position: Int, parent: ViewGroup, dropdown: Boolean): View {
        val dictLang = getItem(position)
        val inflater = LayoutInflater.from(context)
        return if (dropdown) {
            val binding = ItemSpinnerDropdownSearchTypeBinding.inflate(inflater, parent, false)
            binding.tvName.text = dictLang.name
            binding.root
        } else {
            val binding = ItemSpinnerDictLangBinding.inflate(inflater, parent, false)
            binding.tvName.text = dictLang.code.uppercase(Locale.getDefault())
            binding.root
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent, true)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent, false)
    }
}
