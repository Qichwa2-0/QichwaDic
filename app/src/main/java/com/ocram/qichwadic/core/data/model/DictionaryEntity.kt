package com.ocram.qichwadic.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.ColumnInfo
import com.ocram.qichwadic.core.domain.model.DictionaryModel

@Entity(tableName = "dictionary")
data class DictionaryEntity(
        @PrimaryKey
        var id: Int = 0,

        @ColumnInfo
        var name: String? = null,

        @ColumnInfo
        var author: String? = null,

        @ColumnInfo
        var description: String? = null,

        @ColumnInfo(name = "language_begin")
        var languageBegin: String? = null,

        @ColumnInfo(name = "language_end")
        var languageEnd: String? = null,

        @ColumnInfo(name = "is_quechua")
        var isQuechua: Boolean = false,

        @ColumnInfo(name = "total_entries")
        var totalEntries: Int = 0
) {
        fun toDictionaryModel(): DictionaryModel {
                return DictionaryModel(
                        id,
                        name,
                        author,
                        description,
                        languageBegin,
                        languageEnd,
                        isQuechua,
                        totalEntries
                )
        }

        companion object {
                fun fromDictionaryModel(dictionaryModel: DictionaryModel): DictionaryEntity {
                        return DictionaryEntity(
                                dictionaryModel.id,
                                dictionaryModel.name,
                                dictionaryModel.author,
                                dictionaryModel.description,
                                dictionaryModel.languageBegin,
                                dictionaryModel.languageEnd,
                                dictionaryModel.isQuechua,
                                dictionaryModel.totalEntries
                        )
                }
        }
}
