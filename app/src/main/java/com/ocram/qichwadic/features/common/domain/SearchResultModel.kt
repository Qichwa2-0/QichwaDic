package com.ocram.qichwadic.features.common.domain

data class SearchResultModel(
        var dictionaryId: Int = 0,
        var dictionaryName: String? = null,
        var total: Int = 0,
        var definitions: MutableList<DefinitionModel> = mutableListOf()
) : Comparable<SearchResultModel> {

    override fun compareTo(other: SearchResultModel): Int {
        return this.dictionaryId - other.dictionaryId
    }

    companion object {
        fun empty(): SearchResultModel {
            return SearchResultModel()
        }
    }
}
