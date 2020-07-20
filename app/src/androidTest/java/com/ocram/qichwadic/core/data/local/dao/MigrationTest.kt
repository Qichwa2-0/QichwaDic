package com.ocram.qichwadic.core.data.local.dao

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.ocram.qichwadic.core.data.model.DictionaryEntity

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.io.IOException
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (1, 'test', 'author', 'mock dic', 'quechua', 'spanish', 1, 300), " +
                "(2, 'test2', 'author2', 'mock dic', 'spanish', 'quechua', 0, 500)")
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (1, 'allpa', 'sasass', 'asasas', 'ffssf', 1),(2, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(3, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2),(4, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val dictionariesTotal = db.query("SELECT * from dictionary").count
        val definitionsTotal = db.query("SELECT * from definition").count
        val definitionsInDic1Total = db.query("SELECT * from definition where dictionary_id = 1").count
        val definitionsInDic2Total = db.query("SELECT * from definition where dictionary_id = 2").count

        db.close()
        Assert.assertEquals(2, dictionariesTotal.toLong())
        Assert.assertEquals(4, definitionsTotal.toLong())
        Assert.assertEquals(2, definitionsInDic1Total.toLong())
        Assert.assertEquals(2, definitionsInDic2Total.toLong())
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2)

        // db has schema version 2. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (1, 'test', 'author', 'mock dic', 'quechua', 'castellano', 1, 300), " +
                "(2, 'test2', 'author2', 'mock dic', 'castellano', 'quechua', 0, 500)")
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (1, 'allpa', 'sasass', 'asasas', 'ffssf', 1),(2, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(3, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2),(4, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 3 and provide MIGRATION_2_3 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)

        val dictionaries = ArrayList<DictionaryEntity>()
        db.query("SELECT id, language_begin, language_end from dictionary").use { dictCursor ->
            while (dictCursor.moveToNext()) {
                val dictionary = DictionaryEntity()
                dictionary.id = dictCursor.getInt(0)
                dictionary.languageBegin = dictCursor.getString(1)
                dictionary.languageEnd = dictCursor.getString(2)
                dictionaries.add(dictionary)
            }
        }

        Assert.assertEquals(2, dictionaries.size.toLong())
        for (dictionary in dictionaries) {
            if (dictionary.id == 1) {
                Assert.assertEquals("qu", dictionary.languageBegin)
                Assert.assertEquals("es", dictionary.languageEnd)
            } else {
                Assert.assertEquals("es", dictionary.languageBegin)
                Assert.assertEquals("qu", dictionary.languageEnd)
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate3to4() {
        var db = helper.createDatabase(TEST_DB, 3)

        // db has schema version 3. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (4, 'test', 'author', 'mock dic', 'qu', 'es', 1, 300), " +
                "(7, 'test2', 'author2', 'mock dic', 'es', 'qu', 0, 500)")
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (200, 'allpa', 'sasass', 'asasas', 'ffssf', 1)," +
                "(204, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(1204, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2)," +
                "(1209, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)")

        db.execSQL("INSERT INTO `favorite` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " + "values (200, 'allpa', 'sasass', 'asasas', 'ffssf', 1)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 4 and provide MIGRATION_3_4 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, AppDatabase.MIGRATION_3_4)

        db.query("SELECT id from definition").use { dictCursor ->
            var newId = 1
            while (dictCursor.moveToNext()) {
                val id = dictCursor.getInt(0)
                Assert.assertEquals(id.toLong(), newId.toLong())
                newId++
            }
        }

        db.query("SELECT id from favorite").use { dictCursor ->
            var newId = 1
            while (dictCursor.moveToNext()) {
                val id = dictCursor.getInt(0)
                Assert.assertEquals(id.toLong(), newId.toLong())
                newId++
            }
        }
    }

    companion object {

        private const val TEST_DB = "migration-test"
    }
}
