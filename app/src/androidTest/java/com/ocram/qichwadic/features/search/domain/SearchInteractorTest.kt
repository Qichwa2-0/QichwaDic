package com.ocram.qichwadic.features.search.domain

import android.content.Context

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ocram.qichwadic.core.data.remote.ApiResponse
import com.ocram.qichwadic.core.data.remote.CloudDataStore

import com.ocram.qichwadic.features.search.data.datastore.SearchLocalDataStoreImpl
import com.ocram.qichwadic.features.search.data.repository.SearchRepositoryImpl
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.local.dao.AppDatabase
import com.ocram.qichwadic.features.search.data.datastore.SearchCloudDataStore
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.SearchParams
import com.ocram.qichwadic.features.search.data.SearchType
import kotlinx.coroutines.runBlocking

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchInteractorTest {

    private var appDatabase: AppDatabase? = null
    private var searchInteractor: SearchInteractor? = null

    private object DummyCloudDataStore: CloudDataStore(), SearchCloudDataStore {
        override suspend fun search(fromQuechua: Int, target: String, word: String, searchType: Int): ApiResponse<out List<SearchResultEntity>> {
            return ApiResponse.Success(emptyList())
        }

        override suspend fun fetchMoreResults(dictionaryId: Int, word: String, searchType: Int, page: Int): ApiResponse<out SearchResultEntity> {
            return ApiResponse.Success(SearchResultEntity())
        }
    }

    @Before
    fun createDb() {
        appDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        val sharedPreferences = ApplicationProvider.getApplicationContext<Context>().getSharedPreferences("QICHWADIC", Context.MODE_PRIVATE)


        val preferencesHelper = PreferencesHelper(sharedPreferences)
        preferencesHelper.saveOfflineSearchMode(true)

        val searchRepository = SearchRepositoryImpl(SearchLocalDataStoreImpl(appDatabase!!.definitionDao()), DummyCloudDataStore)
        searchInteractor = SearchInteractorImpl(searchRepository)
    }

    @Test
    fun testSearchStartsWith() {
        val wordToSearch = "wasi"

        val dictionary = DictionaryEntity()
        dictionary.id = 1
        dictionary.isQuechua = true
        dictionary.languageBegin = "qu"
        dictionary.languageEnd = "es"
        dictionary.totalEntries = 2

        val definition = DefinitionModel()
        definition.id = 1
        definition.word = "wasi"
        definition.meaning = "casa"
        definition.dictionaryId = dictionary.id

        val definition2 = DefinitionModel()
        definition2.id = 2
        definition2.word = "qullqi"
        definition2.meaning = "plata"
        definition2.dictionaryId = dictionary.id

        val definition3 = DefinitionModel()
        definition3.id = 3
        definition3.word = "maki"
        definition3.meaning = "mano"
        definition3.dictionaryId = dictionary.id

        appDatabase!!.dictionaryDao().insertDictionaryAndDefinitions(
                dictionary,
                listOf(definition, definition2, definition3).map { DefinitionEntity.fromDefinitionModel(it) }
        )

        runBlocking {
            val searchParams = SearchParams(SearchType.STARTS_WITH.type, true, "es", wordToSearch)
            val searchResults = searchInteractor?.queryWordOffline(searchParams) ?: emptyList()
            Assert.assertFalse(searchResults.isEmpty())
            val searchResult = searchResults[0]
            Assert.assertEquals(searchResult.dictionaryId.toLong(), dictionary.id.toLong())
        }


    }

    @Test
    fun testSearchStartsWithAFromQuechua() {

        val dictionary = DictionaryEntity()
        dictionary.id = 1
        dictionary.isQuechua = true
        dictionary.languageBegin = "qu"
        dictionary.languageEnd = "es"
        dictionary.totalEntries = 2

        val fakeDefinitions = mutableListOf<DefinitionModel>()
        for (i in 0..80) {
            val definition = DefinitionModel()
            definition.id = i + 1
            definition.word = "a$i"
            definition.meaning = "a" + i + "es"
            definition.dictionaryId = dictionary.id
            fakeDefinitions.add(definition)
        }
        appDatabase!!.dictionaryDao().insertDictionaryAndDefinitions(
                dictionary,
                fakeDefinitions.map { DefinitionEntity.fromDefinitionModel(it) }
        )

        runBlocking {
            val searchParams = SearchParams(SearchType.STARTS_WITH.type, true, "es", "a")
            val searchResults = searchInteractor?.queryWordOffline(searchParams) ?: emptyList()
            Assert.assertFalse(searchResults.isEmpty())

            val searchResult = searchResults[0]
            Assert.assertEquals(20, searchResult.definitions.size.toLong())
        }

    }

    @Test
    fun testFetchMoreResults() {

        val dictionary = DictionaryEntity()
        dictionary.id = 1
        dictionary.isQuechua = true
        dictionary.languageBegin = "qu"
        dictionary.languageEnd = "es"
        dictionary.totalEntries = 2

        val fakeDefinitions = mutableListOf<DefinitionModel>()
        for (i in 0..80) {
            val definition = DefinitionModel()
            definition.id = i + 1
            definition.word = "a" + (i + 1)
            definition.meaning = "a" + (i + 1) + "es"
            definition.dictionaryId = dictionary.id
            fakeDefinitions.add(definition)
        }
        appDatabase!!.dictionaryDao().insertDictionaryAndDefinitions(
                dictionary,
                fakeDefinitions.map { DefinitionEntity.fromDefinitionModel(it) }
        )

        runBlocking {
            val searchResults = searchInteractor!!.fetchMoreResults(true, dictionary.id, SearchType.STARTS_WITH.type, "a", 2)
            Assert.assertFalse(searchResults.isEmpty())

            Assert.assertEquals(20, searchResults.size.toLong())
            for (i in 1..searchResults.size) {
                val definition = searchResults[i - 1]
                Assert.assertEquals((20 + i).toLong(), definition.id.toLong())
                Assert.assertEquals("a" + (20 + i), definition.word)
            }
        }
    }
}
