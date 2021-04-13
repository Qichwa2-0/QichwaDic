package com.ocram.qichwadic.core.ui

class SearchParams {

    var searchTypePos: Int = 0
    var isFromQuechua: Boolean = false
    var nonQuechuaLangCode: String
    var searchWord: String = ""

    constructor(searchTypePos: Int, fromQuechua: Boolean, nonQuechuaLangCode: String, searchWord: String = "") {
        this.searchTypePos = searchTypePos
        this.isFromQuechua = fromQuechua
        this.nonQuechuaLangCode = nonQuechuaLangCode
        this.searchWord = searchWord
    }

    constructor() {
        this.searchTypePos = 2
        this.isFromQuechua = true
        this.nonQuechuaLangCode = "es"
        this.searchWord = "a"
    }

    companion object {

        val MAX_SEARCH_RESULTS = 20
    }
}
