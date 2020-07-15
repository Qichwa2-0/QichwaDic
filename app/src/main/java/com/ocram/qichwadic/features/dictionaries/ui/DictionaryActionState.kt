package com.ocram.qichwadic.features.dictionaries.ui

import com.ocram.qichwadic.features.common.domain.DictionaryModel


data class DictionaryActionState(
        val pos: Int,
        val dictionary: DictionaryModel,
        val error: Boolean = false
)
