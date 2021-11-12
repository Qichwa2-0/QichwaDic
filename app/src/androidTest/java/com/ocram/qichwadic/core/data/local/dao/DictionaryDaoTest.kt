package com.ocram.qichwadic.core.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.model.DictionaryWithStatusEntity
import com.ocram.qichwadic.core.data.model.toDictionaryModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DictionaryDaoTest {
    private lateinit var dictionaryDao: DictionaryDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        dictionaryDao = db.dictionaryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testGetDictionariesNoDefinitions() {
        val dictionaries = mutableListOf<DictionaryEntity>()
        repeat(10) {
            dictionaries += DictionaryEntity(id = it, name = "Dictionary $it")
        }
        var collectedDictionaryEntities: List<DictionaryWithStatusEntity>
        runBlocking {
            dictionaryDao.insertDictionaries(dictionaries)
            collectedDictionaryEntities = dictionaryDao.getDictionariesWithStatus().first()
        }
        Assert.assertEquals(10, collectedDictionaryEntities.size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetDictionariesWithSomeDefinitions() {
        generateData()

        var collectedDictionaryEntities: List<DictionaryWithStatusEntity>
        runBlocking {
            collectedDictionaryEntities = dictionaryDao.getDictionariesWithStatus().first()
        }
        Assert.assertTrue(
            collectedDictionaryEntities
                .filter { it.dictionaryEntity.id % 2 == 0 }
                .all { it.hasDefinitionsSaved }
        )
    }

    @Test
    fun testRemoveDefinitionsForDictionary() {

        generateData()

        var dictionaryWithStatus: List<DictionaryWithStatusEntity>

        runBlocking { dictionaryWithStatus = dictionaryDao.getDictionariesWithStatus().first() }

        Assert.assertTrue(dictionaryWithStatus.first().hasDefinitionsSaved)

        val dictionaryId = dictionaryWithStatus.first().dictionaryEntity.id
        runBlocking {
            dictionaryDao.removeDefinitions(dictionaryId)
            dictionaryWithStatus = dictionaryDao.getDictionariesWithStatus().first()
        }

        val previousDictionary = dictionaryWithStatus.find { it.dictionaryEntity.id == dictionaryId }

        Assert.assertNotNull("Dictionary shouldn't be deleted", previousDictionary)

        previousDictionary?.let {
            Assert.assertFalse("But definitions are deleted", it.hasDefinitionsSaved)
            Assert.assertNotEquals(
                "Dictionary must not be  in first place because it has no definitions",
                it.dictionaryEntity.id,
                dictionaryWithStatus.first().dictionaryEntity.id
            )
        }

    }

    @Test
    fun testDictionariesSort() {
        val totalDictionaries = 15
        val totalDictsWithDefinitions = 5
        // last 5 will have definitions saved
        generateData(totalDictionaries = totalDictionaries) {
            it >= (totalDictionaries - totalDictsWithDefinitions)
        }
        var dictionaries = emptyList<DictionaryModel>()
        runBlocking {
           dictionaries = dictionaryDao.getDictionariesWithStatus() .first().map {
               it.toDictionaryModel()
           }
        }
        Assert.assertTrue(
            "5 dicts with definitions should come first",
            dictionaries.take(totalDictsWithDefinitions).all { it.existsInLocal }
        )
        Assert.assertTrue(
            "After 5th dictionary, all should be considered as not local",
            dictionaries.drop(totalDictsWithDefinitions).none { it.existsInLocal }
        )
    }

    private fun generateData(
        totalDictionaries: Int = 10,
        condForHavingDefinitions: (index: Int) -> Boolean = { it % 2  == 0 }
    ) {
        val dictionaries = mutableListOf<DictionaryEntity>()
        val definitions = mutableListOf<DefinitionEntity>()
        (0 until totalDictionaries).forEach { dictionaryCounter ->
            dictionaries += DictionaryEntity(dictionaryCounter, "Dictionary $dictionaryCounter")
            if (condForHavingDefinitions(dictionaryCounter)) {
                repeat(50) { definitionCounter ->
                    definitions += DefinitionEntity(
                        id = (definitionCounter * dictionaryCounter),
                        dictionaryId = dictionaryCounter
                    )
                }
            }
        }
        runBlocking {
            dictionaryDao.insertDictionaries(dictionaries)
            dictionaryDao.insertDefinitions(definitions)
        }
    }
}
