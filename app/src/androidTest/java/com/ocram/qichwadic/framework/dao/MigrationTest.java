package com.ocram.qichwadic.framework.dao;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.database.Cursor;

import com.ocram.qichwadic.domain.model.Dictionary;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {

    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                Objects.requireNonNull(AppDatabase.class.getCanonicalName()),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);

        // db has schema version 1. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (1, 'test', 'author', 'mock dic', 'quechua', 'spanish', 1, 300), " +
                "(2, 'test2', 'author2', 'mock dic', 'spanish', 'quechua', 0, 500)");
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (1, 'allpa', 'sasass', 'asasas', 'ffssf', 1),(2, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(3, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2),(4, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2);

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        int totalDics = db.query("SELECT * from dictionary").getCount();
        int totalDefs = db.query("SELECT * from definition").getCount();
        int totalDefsDic1 = db.query("SELECT * from definition where dictionary_id = 1").getCount();
        int totalDefsDic2 = db.query("SELECT * from definition where dictionary_id = 2").getCount();

        db.close();
        Assert.assertEquals(2, totalDics);
        Assert.assertEquals(4, totalDefs);
        Assert.assertEquals(2, totalDefsDic1);
        Assert.assertEquals(2, totalDefsDic2);
    }

    @Test
    public void migrate2To3() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);

        // db has schema version 2. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (1, 'test', 'author', 'mock dic', 'quechua', 'castellano', 1, 300), " +
                "(2, 'test2', 'author2', 'mock dic', 'castellano', 'quechua', 0, 500)");
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (1, 'allpa', 'sasass', 'asasas', 'ffssf', 1),(2, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(3, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2),(4, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 3 and provide MIGRATION_2_3 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3);

        List<Dictionary> dictionaries = new ArrayList<>();
        try (Cursor dictCursor = db.query("SELECT id, language_begin, language_end from dictionary")) {
            while(dictCursor.moveToNext()) {
                Dictionary dictionary = new Dictionary();
                dictionary.setId(dictCursor.getInt(0));
                dictionary.setLanguageBegin(dictCursor.getString(1));
                dictionary.setLanguageEnd(dictCursor.getString(2));
                dictionaries.add(dictionary);
            }
        }

        Assert.assertEquals(2, dictionaries.size());
        for(Dictionary dictionary : dictionaries) {
            if(dictionary.getId() == 1) {
                Assert.assertEquals("qu", dictionary.getLanguageBegin());
                Assert.assertEquals("es", dictionary.getLanguageEnd());
            } else {
                Assert.assertEquals("es", dictionary.getLanguageBegin());
                Assert.assertEquals("qu", dictionary.getLanguageEnd());
            }
        }
    }

    @Test
    public void migrate3to4() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);

        // db has schema version 3. insert some data using SQL queries.
        // You cannot use DAO classes because they expect the latest schema.
        db.execSQL("INSERT INTO `dictionary` (`id`, `name`, `author`, `description`, `language_begin`, `language_end`, `is_quechua`, `total_entries`) " +
                "values (4, 'test', 'author', 'mock dic', 'qu', 'es', 1, 300), " +
                "(7, 'test2', 'author2', 'mock dic', 'es', 'qu', 0, 500)");
        db.execSQL("INSERT INTO `definition` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (200, 'allpa', 'sasass', 'asasas', 'ffssf', 1)," +
                "(204, 'allpaddd', 'sasass', 'asasas', 'ffssf', 1), " +
                "(1204, 'allfdfdfpa', 'sasass', 'asasas', 'ffssf', 2)," +
                "(1209, 'allpaddd', 'sasass', 'asasas', 'ffssf', 2)");

        db.execSQL("INSERT INTO `favorite` (`id`, `word`, `meaning`, `summary`, `dictionary_name`, `dictionary_id`) " +
                "values (200, 'allpa', 'sasass', 'asasas', 'ffssf', 1)");

        // Prepare for the next version.
        db.close();

        // Re-open the database with version 4 and provide MIGRATION_3_4 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, AppDatabase.MIGRATION_3_4);

        try (Cursor dictCursor = db.query("SELECT id from definition")) {
            int newId = 1;
            while(dictCursor.moveToNext()) {
                int id = dictCursor.getInt(0);
                Assert.assertEquals(id, newId);
                newId++;
            }
        }

        try (Cursor dictCursor = db.query("SELECT id from favorite")) {
            int newId = 1;
            while(dictCursor.moveToNext()) {
                int id = dictCursor.getInt(0);
                Assert.assertEquals(id, newId);
                newId++;
            }
        }
    }
}
