package com.ocram.qichwadic.core.domain.model

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

    /**
     * Will sort items by status (existsInLocal) then by id
     */
    override fun compareTo(other: DictionaryModel) = when {
        this.existsInLocal && !other.existsInLocal -> -1
        !this.existsInLocal && other.existsInLocal ->   1
        else -> this.id - other.id
    }

    fun copy(
        id: Int = this.id,
        name: String? = this.name,
        author: String? = this.author,
        description: String? = this.description,
        languageBegin: String? = this.languageBegin,
        languageEnd: String? = this.languageEnd,
        isQuechua: Boolean = this.isQuechua,
        totalEntries: Int = this.totalEntries,
        existsInLocal: Boolean = this.existsInLocal,
        downloading: Boolean = this.downloading
    ) = DictionaryModel(
        id = id,
        name = name,
        author = author,
        description = description,
        languageBegin = languageBegin,
        languageEnd = languageEnd,
        isQuechua = isQuechua,
        totalEntries = totalEntries,
        existsInLocal = existsInLocal,
        downloading = downloading
    )
}