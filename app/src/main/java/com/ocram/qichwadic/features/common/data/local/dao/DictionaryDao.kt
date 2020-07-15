package com.ocram.qichwadic.features.common.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.features.common.data.model.DefinitionEntity
import com.ocram.qichwadic.features.common.data.model.DictionaryEntity

@Dao
interface DictionaryDao {

    @Query("SELECT d.* from dictionary d order by d.is_quechua DESC, d.id")
    fun getDictionaries(): List<DictionaryEntity>

    @Query("DELETE from dictionary where id = :id")
    fun removeOne(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>)

}
