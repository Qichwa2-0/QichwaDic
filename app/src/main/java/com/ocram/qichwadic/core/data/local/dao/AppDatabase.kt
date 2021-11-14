package com.ocram.qichwadic.core.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.util.Log
import androidx.room.Room

import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.model.FavoriteEntity

@Database(entities = [DictionaryEntity::class, DefinitionEntity::class, FavoriteEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao
    abstract fun searchDao(): SearchDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "simitaqidb"
                )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                        .build()
                INSTANCE = instance
                return instance
            }
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `favorite` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`word` TEXT, " +
                        "`meaning` TEXT, " +
                        "`summary` TEXT, " +
                        "`dictionary_name` TEXT, " +
                        "`dictionary_id` INTEGER NOT NULL, " +
                        " PRIMARY KEY (`id`))")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("MIGRATION", "Executing migration 2 to 3")
                database.execSQL("UPDATE `dictionary` SET language_begin = 'qu' WHERE language_begin = 'Quechua' COLLATE NOCASE")
                database.execSQL("UPDATE `dictionary` SET language_begin = 'es' WHERE language_begin = 'Castellano' COLLATE NOCASE or language_end = 'Español' COLLATE NOCASE")
                database.execSQL("UPDATE `dictionary` SET language_end ='es' WHERE language_end = 'Castellano' COLLATE NOCASE or language_end = 'Español' COLLATE NOCASE")
                database.execSQL("UPDATE `dictionary` SET language_end = 'qu' WHERE language_end = 'Quechua' COLLATE NOCASE")
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("MIGRATION", "Executing migration 3 to 4")
                database.execSQL("CREATE TABLE `temp_definition` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "`word` TEXT, " +
                        "`meaning` TEXT, " +
                        "`summary` TEXT, " +
                        "`dictionary_name` TEXT, " +
                        "`dictionary_id` INTEGER NOT NULL, " +
                        "FOREIGN KEY (`dictionary_id`) REFERENCES `dictionary`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                database.execSQL("DROP INDEX `index_definition_dictionary_id`")
                database.execSQL("CREATE INDEX `index_definition_dictionary_id` ON temp_definition(dictionary_id)")

                database.execSQL("INSERT INTO `temp_definition` (word, meaning, summary, dictionary_name, dictionary_id) " + "SELECT word, meaning, summary, dictionary_name, dictionary_id FROM definition ORDER BY dictionary_id, id")

                database.execSQL("DROP TABLE definition")
                database.execSQL("ALTER TABLE temp_definition RENAME TO definition")

                database.execSQL("CREATE TABLE `temp_favorite` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , " +
                        "`word` TEXT, " +
                        "`meaning` TEXT, " +
                        "`summary` TEXT, " +
                        "`dictionary_name` TEXT, " +
                        "`dictionary_id` INTEGER NOT NULL)")

                database.execSQL("INSERT INTO `temp_favorite` (word, meaning, summary, dictionary_name, dictionary_id) " + "SELECT word, meaning, summary, dictionary_name, dictionary_id FROM favorite ORDER BY dictionary_id, id")
                database.execSQL("DROP TABLE favorite")
                database.execSQL("ALTER TABLE temp_favorite RENAME TO favorite")
            }
        }
    }
}
