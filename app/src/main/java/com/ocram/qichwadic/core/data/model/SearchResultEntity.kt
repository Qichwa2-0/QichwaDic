package com.ocram.qichwadic.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.ocram.qichwadic.core.domain.model.SearchResultModel

data class SearchResultEntity (
        @Expose
        @ColumnInfo(name = "dictionary_id")
        var dictionaryId: Int = 0,


        @Expose
        @ColumnInfo(name = "dictionary_name")
        var dictionaryName: String? = null,

        @Expose
        @ColumnInfo(name = "total")
        var total: Int = 0,

        @Expose
        @Ignore
        var definitions: MutableList<DefinitionEntity> = mutableListOf()
)