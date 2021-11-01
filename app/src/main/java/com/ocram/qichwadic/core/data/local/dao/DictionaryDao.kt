package com.ocram.qichwadic.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity

@Dao
interface DictionaryDao {

    @Query("SELECT d.* from dictionary d order by d.is_quechua DESC, d.id")
    suspend fun getDictionaries(): List<DictionaryEntity>

    @Query("DELETE from dictionary where id = :id")
    suspend fun removeOne(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDictionaryAndDefinitions(dictionary: DictionaryEntity, definitions: List<DefinitionEntity>)

}
