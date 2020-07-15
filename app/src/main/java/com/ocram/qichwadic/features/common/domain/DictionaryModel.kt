package com.ocram.qichwadic.features.common.domain

import java.util.*

class DictionaryModel(
        val id: Int = 0,
        var name: String? = null,
        var author: String? = null,
        var description: String? = null,
        var languageBegin: String? = null,
        var languageEnd: String? = null,
        val isQuechua: Boolean = false,
        val totalEntries: Int = 0,
        var existsInLocal: Boolean = false,
        var downloading: Boolean = false
) : Comparable<DictionaryModel> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as DictionaryModel?
        return id == that!!.id
    }

    override fun hashCode(): Int {

        return Objects.hash(id)
    }

    override fun compareTo(other: DictionaryModel)
            = compareValuesBy(this, other, { it.existsInLocal }, { other.id })
}