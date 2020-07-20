package com.ocram.qichwadic.core.ui

class SearchParams {

    var searchTypePos: Int = 0
    var isFromQuechua: Boolean = false
    var nonQuechuaLangCode: String? = null

    constructor(searchTypePos: Int, fromQuechua: Boolean, nonQuechuaLangCode: String) {
        this.searchTypePos = searchTypePos
        this.isFromQuechua = fromQuechua
        this.nonQuechuaLangCode = nonQuechuaLangCode
    }

    constructor() {
        this.searchTypePos = 2
        this.isFromQuechua = true
        this.nonQuechuaLangCode = "es"
    }

    companion object {

        val MAX_SEARCH_RESULTS = 20
    }
}
