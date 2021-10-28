package com.ocram.qichwadic.core.ui

data class SearchParams(
    var searchTypePos: Int = 2,
    var isFromQuechua: Boolean = true,
    var nonQuechuaLangCode: String = "es",
    var searchWord: String = ""
)