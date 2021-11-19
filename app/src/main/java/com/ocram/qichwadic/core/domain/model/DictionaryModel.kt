package com.ocram.qichwadic.core.domain.model

data class DictionaryModel(
        val id: Int = 0,
        var name: String? = null,
        var author: String? = null,
        var description: String? = null,
        var languageBegin: String? = null,
        var languageEnd: String? = null,
        val isQuechua: Boolean = false,
        val totalEntries: Int = 0,
        var existsInLocal: Boolean = false,
)