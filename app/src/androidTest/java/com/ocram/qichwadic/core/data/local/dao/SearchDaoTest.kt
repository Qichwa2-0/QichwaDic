package com.ocram.qichwadic.core.data.local.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class SearchDaoTest: BaseDbTest() {

    @Test
    fun testGetDictionariesContainingWord() {
        val dictionaries = listOf(
            DictionaryEntity(id = 1, name = "Dictionary 1", languageBegin = "qu", languageEnd = "es"),
            DictionaryEntity(id = 2, name = "Dictionary 2", languageBegin = "es", languageEnd = "qu")
        )
        val definitions = listOf(
            DefinitionEntity(id = 1, word = "rumi", dictionaryId = 1),
            DefinitionEntity(id = 2, word = "allpa", dictionaryId = 1),
            DefinitionEntity(id = 3, word = "kallpa", dictionaryId = 1),
            DefinitionEntity(id = 4, word = "mallpa", dictionaryId = 2)
        )
        val results: List<SearchResultEntity>
        runBlocking {
            db.dictionaryDao().insertDictionaries(dictionaries)
            db.dictionaryDao().insertDefinitions(definitions)
            results =
                db.searchDao().getDictionariesContainingWord(langBegin = "qu", langEnd = "es", "%allpa")
        }
        Assert.assertEquals("results must belong to dictionary 1", 1, results.size)
    }

    @Test
    fun testFindDefinitionsLike() {
        val dictionaries = listOf(
            DictionaryEntity(id = 1, name = "Dictionary 1", languageBegin = "qu", languageEnd = "es"),
            DictionaryEntity(id = 2, name = "Dictionary 2", languageBegin = "es", languageEnd = "qu")
        )
        val definitions = (1..24).map { DefinitionEntity(id = it, word = "ab$it", dictionaryId = 1) }
        val results: List<DefinitionEntity>
        runBlocking {
            db.dictionaryDao().insertDictionaries(dictionaries)
            db.dictionaryDao().insertDefinitions(definitions)
            results =
                db.searchDao().findDefinitionsLike(1, searchText = "a%", offset = 20)
        }
        Assert.assertEquals("4 results from offset 20", 4, results.size)
    }

    @Test
    fun testFindDefinitionsInAllDictionaries() {
        val dictionaries = listOf(
            DictionaryEntity(id = 1, name = "Dictionary 1", languageBegin = "qu", languageEnd = "es"),
            DictionaryEntity(id = 2, name = "Dictionary 2", languageBegin = "es", languageEnd = "qu")
        )
        val definitions = (1..24).map { DefinitionEntity(id = it, word = "ab$it", dictionaryId = 1) }

        val results: List<DefinitionEntity>

        val query = "SELECT * FROM " +
                "(SELECT de.* " +
                "FROM definition de INNER JOIN dictionary di ON de.dictionary_id = di.id " +
                "WHERE di.id = 1 AND de.word LIKE \"a%\" " +
                "ORDER BY de.id LIMIT 20)"
        runBlocking {
            db.dictionaryDao().insertDictionaries(dictionaries)
            db.dictionaryDao().insertDefinitions(definitions)
            results =
                db.searchDao().findDefinitionsInAllDictionaries(SimpleSQLiteQuery(query))
        }
        Assert.assertEquals("First 20 results for dictionary 1", 20, results.size)
    }
}