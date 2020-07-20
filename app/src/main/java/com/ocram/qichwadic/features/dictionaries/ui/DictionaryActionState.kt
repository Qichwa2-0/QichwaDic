package com.ocram.qichwadic.features.dictionaries.ui

import com.ocram.qichwadic.core.domain.model.DictionaryModel


data class DictionaryActionState(
        val pos: Int,
        val dictionary: DictionaryModel,
        val error: Boolean = false
)
