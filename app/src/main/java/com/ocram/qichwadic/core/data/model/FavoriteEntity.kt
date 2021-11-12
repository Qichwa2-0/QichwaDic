package com.ocram.qichwadic.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @ColumnInfo(name = "word")
        var word: String? = null,

        @ColumnInfo(name = "meaning")
        var meaning: String? = null,

        @ColumnInfo(name = "summary")
        var summary: String? = null,

        @ColumnInfo(name = "dictionary_name")
        var dictionaryName: String? = null,

        @ColumnInfo(name = "dictionary_id")
        var dictionaryId: Int = 0
){ companion object }

