package com.ocram.qichwadic.features.common.domain

data class DefinitionModel (
        var id: Int = 0,
        var word: String? = null,
        var meaning: String? = null,
        var summary: String? = null,
        var dictionaryName: String? = null,
        var dictionaryId: Int = 0
)