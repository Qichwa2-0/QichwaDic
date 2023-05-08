package com.ocram.qichwadic.features.search.data

enum class SearchType constructor(val type: Int, private val wordFormat: String) {

    CONTAINS(0, "%%%s%%"),
    EXACT(1, ""),
    STARTS_WITH(2, "%s%%"),
    ENDS_WITH(3, "%%%s");


    companion object {

        fun buildSearchCriteria(type: Int, entry: String): String {
            for (searchType in values()) {
                if (type == EXACT.type) return entry

                if (type == searchType.type) {
                    return String.format(searchType.wordFormat, entry)
                }
            }
            return entry
        }
    }
}