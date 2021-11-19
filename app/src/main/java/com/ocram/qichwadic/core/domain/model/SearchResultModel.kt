package com.ocram.qichwadic.core.domain.model

data class SearchResultModel(
        var dictionaryId: Int = 0,
        var dictionaryName: String? = null,
        var total: Int = 0,
        var definitions: MutableList<DefinitionModel> = mutableListOf(),
) : Comparable<SearchResultModel> {

    val titleWithTotal get() = "(${total}) $dictionaryName"

    override fun compareTo(other: SearchResultModel): Int {
        return this.dictionaryId - other.dictionaryId
    }

    fun hasMoreDefinitions() = definitions.size < total
}

