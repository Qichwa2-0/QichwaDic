package com.ocram.qichwadic.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.model.DictionaryWithStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {

    @Query("SELECT d.*, d.id == x.dictId as hasDefinitionsSaved FROM dictionary d LEFT JOIN (SELECT de.dictionary_id as dictId FROM definition de GROUP BY de.dictionary_id) x ON d.id = x.dictId ORDER by hasDefinitionsSaved DESC, d.is_quechua DESC, d.id")
    fun getDictionariesWithStatus(): Flow<List<DictionaryWithStatusEntity>>

    @Query("DELETE FROM definition WHERE dictionary_id = :id")
    suspend fun removeDefinitions(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDictionaries(dictionaries: List<DictionaryEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefinitions(definitions: List<DefinitionEntity>)

}
