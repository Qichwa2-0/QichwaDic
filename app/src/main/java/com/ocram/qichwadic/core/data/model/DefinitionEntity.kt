package com.ocram.qichwadic.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ocram.qichwadic.core.domain.model.DefinitionModel

@Entity(
        tableName = "definition",
        foreignKeys = [
            ForeignKey(
                    entity = DictionaryEntity::class,
                    parentColumns = ["id"],
                    childColumns = ["dictionary_id"],
                    onDelete = ForeignKey.CASCADE
            )
        ],
        indices = [Index("dictionary_id")]
)
data class DefinitionEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,

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

) {
    fun toDefinitionModel(): DefinitionModel {
        return DefinitionModel(
                id,
                word,
                meaning,
                summary,
                dictionaryName,
                dictionaryId
        )
    }

    companion object {
        fun fromDefinitionModel(definitionModel: DefinitionModel): DefinitionEntity {
            return DefinitionEntity(
                    definitionModel.id,
                    definitionModel.word,
                    definitionModel.meaning,
                    definitionModel.summary,
                    definitionModel.dictionaryName,
                    definitionModel.dictionaryId
            )
        }
    }
}

//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0

//    @Expose
//    @SerializedName("word")
//    @ColumnInfo(name = "word")
//    var word: String? = null

//    @Expose
//    @SerializedName("meaning")
//    @ColumnInfo(name = "meaning")
//    var meaning: String? = null

//    @Expose
//    @SerializedName("summary")
//    @ColumnInfo(name = "summary")
//    var summary: String? = null
//
//    @Expose
//    @SerializedName("dictionaryName")
//    @ColumnInfo(name = "dictionary_name")
//    var dictionaryName: String? = null

//    @Expose
//    @ColumnInfo(name = "dictionary_id")
//    @SerializedName("dictionaryId")
//    var dictionaryId: Int = 0

