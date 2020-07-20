package com.ocram.qichwadic.core.ui

class DictLang(codeName: String) {

    val code: String
    val name: String

    fun isQuechua(): Boolean {
        return "qu".equals(this.code, ignoreCase = true)
    }

    init {
        val codeNameSplit = codeName.split(SPLIT_DELIMITER)
        this.code = codeNameSplit[0]
        this.name = codeNameSplit[1]
    }

    override fun toString(): String {
        return this.name
    }

    companion object {

        private const val SPLIT_DELIMITER = ";"
    }
}
