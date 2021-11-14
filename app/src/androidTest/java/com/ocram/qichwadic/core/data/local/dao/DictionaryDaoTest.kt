package com.ocram.qichwadic.core.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.model.DictionaryWithStatusEntity
import com.ocram.qichwadic.core.data.model.toDictionaryModel
import com.ocram.qichwadic.core.domain.model.DictionaryModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryDaoTest: BaseDbTest() {
    
    @Test
    @Throws(Exception::class)
    fun testGetDictionariesNoDefinitions() {
        val dictionaries = mutableListOf<DictionaryEntity>()
        repeat(10) {
            dictionaries += DictionaryEntity(id = it, name = "Dictionary $it")
        }
        var collectedDictionaryEntities: List<DictionaryWithStatusEntity>
        runBlocking {
            db.dictionaryDao().insertDictionaries(dictionaries)
            collectedDictionaryEntities = db.dictionaryDao().getDictionariesWithStatus().first()
        }
        Assert.assertEquals(10, collectedDictionaryEntities.size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetDictionariesWithSomeDefinitions() {
        generateDictionariesAndDefinitions()

        var collectedDictionaryEntities: List<DictionaryWithStatusEntity>
        runBlocking {
            collectedDictionaryEntities = db.dictionaryDao().getDictionariesWithStatus().first()
        }
        Assert.assertTrue(
            collectedDictionaryEntities
                .filter { it.dictionaryEntity.id % 2 == 0 }
                .all { it.hasDefinitionsSaved }
        )
    }

    @Test
    fun testRemoveDefinitionsForDictionary() {

        generateDictionariesAndDefinitions()

        var dictionaryWithStatus: List<DictionaryWithStatusEntity>

        runBlocking { dictionaryWithStatus = db.dictionaryDao().getDictionariesWithStatus().first() }

        Assert.assertTrue(dictionaryWithStatus.first().hasDefinitionsSaved)

        val dictionaryId = dictionaryWithStatus.first().dictionaryEntity.id
        runBlocking {
            db.dictionaryDao().removeDefinitions(dictionaryId)
            dictionaryWithStatus = db.dictionaryDao().getDictionariesWithStatus().first()
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
        generateDictionariesAndDefinitions(totalDictionaries) {
            it > 10
        }
        var dictionaries = emptyList<DictionaryModel>()
        runBlocking {
           dictionaries = db.dictionaryDao().getDictionariesWithStatus() .first().map {
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

    @Test
    fun testInsertDictionaries() {
        generateDictionariesAndDefinitions(totalDictionaries = 2) { it == 0 }

        var dictionaries: List<DictionaryWithStatusEntity>
        runBlocking {
            dictionaries = db.dictionaryDao().getDictionariesWithStatus().first()
        }
        Assert.assertEquals(2, dictionaries.size)
    }

    private fun generateDictionariesAndDefinitions(
        totalDictionaries: Int = 10,
        condForHavingDefinitions: (index: Int) -> Boolean = { it % 2  == 0 }
    ) {
        val dictionaries = mutableListOf<DictionaryEntity>()
        val definitions = mutableListOf<DefinitionEntity>()
        (1 .. totalDictionaries).forEach { dictionaryCounter ->
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
            db.dictionaryDao().insertDictionaries(dictionaries)
            db.dictionaryDao().insertDefinitions(definitions)
        }
    }

    @Test
    fun testInsertDefinitions() {
        val dictionaries = listOf(
            DictionaryEntity(id = 1, name = "Dictionary"),
            DictionaryEntity(id = 2, name = "Dictionary 2")
        )
        val definitions = listOf(
            DefinitionEntity(id = 1, word = "word", meaning = "meaning", dictionaryId = 1),
            DefinitionEntity(id = 2, word = "word 2", meaning = "meaning 2", dictionaryId = 1)
        )
        var savedDefinitions: List<DefinitionEntity>
        runBlocking {
            db.dictionaryDao().insertDictionaries(dictionaries)
            db.dictionaryDao().insertDefinitions(definitions)
            savedDefinitions = db.dictionaryDao().getDefinitions(1).first()
        }
        Assert.assertEquals("2 definitions were saved", 2, savedDefinitions.size)
        Assert.assertEquals(definitions, savedDefinitions)
    }
}
