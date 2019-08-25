package com.ocram.qichwadic.framework.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import androidx.annotation.NonNull;
import android.util.Log;

import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.Favorite;

@Database(entities = {Dictionary.class, Definition.class, Favorite.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase{

    public abstract DictionaryDao getDictionaryDao();
    public abstract SearchDao getDefinitionDao();
    public abstract FavoriteDao getFavoriteDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `favorite` (" +
                    "`id` INTEGER NOT NULL, " +
                    "`word` TEXT, " +
                    "`meaning` TEXT, " +
                    "`summary` TEXT, " +
                    "`dictionary_name` TEXT, " +
                    "`dictionary_id` INTEGER NOT NULL, " +
                    " PRIMARY KEY (`id`))");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d("MIGRATION", "Executing migration 2 to 3");
            database.execSQL("UPDATE `dictionary` SET language_begin = 'qu' WHERE language_begin = 'Quechua' COLLATE NOCASE");
            database.execSQL("UPDATE `dictionary` SET language_begin = 'es' WHERE language_begin = 'Castellano' COLLATE NOCASE or language_end = 'Español' COLLATE NOCASE");
            database.execSQL("UPDATE `dictionary` SET language_end ='es' WHERE language_end = 'Castellano' COLLATE NOCASE or language_end = 'Español' COLLATE NOCASE");
            database.execSQL("UPDATE `dictionary` SET language_end = 'qu' WHERE language_end = 'Quechua' COLLATE NOCASE");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d("MIGRATION", "Executing migration 3 to 4");
            database.execSQL("CREATE TABLE `temp_definition` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                    "`word` TEXT, " +
                    "`meaning` TEXT, " +
                    "`summary` TEXT, " +
                    "`dictionary_name` TEXT, " +
                    "`dictionary_id` INTEGER NOT NULL, " +
                    "FOREIGN KEY (`dictionary_id`) REFERENCES `dictionary`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            );
            database.execSQL("DROP INDEX `index_definition_dictionary_id`");
            database.execSQL("CREATE INDEX `index_definition_dictionary_id` ON temp_definition(dictionary_id)");

            database.execSQL("INSERT INTO `temp_definition` (word, meaning, summary, dictionary_name, dictionary_id) " +
                    "SELECT word, meaning, summary, dictionary_name, dictionary_id FROM definition ORDER BY dictionary_id, id");

            database.execSQL("DROP TABLE definition");
            database.execSQL("ALTER TABLE temp_definition RENAME TO definition");

            database.execSQL("CREATE TABLE `temp_favorite` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                    "`word` TEXT, " +
                    "`meaning` TEXT, " +
                    "`summary` TEXT, " +
                    "`dictionary_name` TEXT, " +
                    "`dictionary_id` INTEGER NOT NULL)");

            database.execSQL("INSERT INTO `temp_favorite` (word, meaning, summary, dictionary_name, dictionary_id) " +
                    "SELECT word, meaning, summary, dictionary_name, dictionary_id FROM favorite ORDER BY dictionary_id, id");
            database.execSQL("DROP TABLE favorite");
            database.execSQL("ALTER TABLE temp_favorite RENAME TO favorite");
        }
    };
}
