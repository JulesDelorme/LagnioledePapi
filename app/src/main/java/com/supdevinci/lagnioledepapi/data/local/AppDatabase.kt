package com.supdevinci.lagnioledepapi.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CustomCocktailEntity::class,
        FavoriteCocktailEntity::class,
        UserStatsEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(CustomCocktailConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customCocktailDao(): CustomCocktailDao
    abstract fun favoriteCocktailDao(): FavoriteCocktailDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `favorite_cocktails` (
                        `favorite_key` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `remote_id_or_local_id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `image_url` TEXT,
                        `category` TEXT NOT NULL,
                        `badge` TEXT NOT NULL,
                        `saved_at` INTEGER NOT NULL,
                        PRIMARY KEY(`favorite_key`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_stats` (
                        `id` INTEGER NOT NULL,
                        `detailViews` INTEGER NOT NULL,
                        `surpriseOpens` INTEGER NOT NULL,
                        `copyActions` INTEGER NOT NULL,
                        `shareActions` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT OR IGNORE INTO `user_stats`
                    (`id`, `detailViews`, `surpriseOpens`, `copyActions`, `shareActions`)
                    VALUES (1, 0, 0, 0, 0)
                    """.trimIndent()
                )
            }
        }
    }
}
