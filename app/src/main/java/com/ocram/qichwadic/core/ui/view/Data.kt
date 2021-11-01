package com.ocram.qichwadic.core.ui.view

private const val SPLIT_DELIMITER = ";"

class DictLang(codeName: String) {

    val code: String
    val name: String

    init {
        codeName.split(SPLIT_DELIMITER).apply {
            code = this[0]
            name = this[1]
        }
    }

    val isQuechua get() = "qu".equals(this.code, ignoreCase = true)

    override fun toString(): String {
        return this.name
    }
}
