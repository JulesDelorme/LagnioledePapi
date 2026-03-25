package com.supdevinci.lagnioledepapi.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class AppDatabaseMigrationTest {
    private lateinit var context: Context
    private lateinit var databaseFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        databaseFile = context.getDatabasePath("migration-test.db")
        if (databaseFile.exists()) {
            databaseFile.delete()
        }
    }

    @After
    fun tearDown() {
        if (databaseFile.exists()) {
            databaseFile.delete()
        }
    }

    @Test
    fun migration_1_2_preservesCustomCocktailsAndCreatesNewTables() = runTest {
        val dbV1 = SQLiteDatabase.openOrCreateDatabase(databaseFile, null)
        dbV1.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `custom_cocktails` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `story` TEXT NOT NULL,
                `ingredients_json` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        dbV1.execSQL(
            """
            INSERT INTO `custom_cocktails` (`id`, `name`, `story`, `ingredients_json`, `createdAt`)
            VALUES (1, 'Le Barda du PMU', 'Inventé au comptoir', '[{"name":"Rhum","dose":"4 cl"}]', 1234)
            """.trimIndent()
        )
        dbV1.version = 1
        dbV1.close()

        val migratedDb = Room.databaseBuilder(context, AppDatabase::class.java, databaseFile.name)
            .allowMainThreadQueries()
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

        val cocktail = migratedDb.customCocktailDao().getById(1L)
        val stats = migratedDb.userStatsDao().getById()
        val favoritesCount = migratedDb.favoriteCocktailDao().observeFavoritesCount().first()

        assertNotNull(cocktail)
        assertEquals("Le Barda du PMU", cocktail?.name)
        assertNotNull(stats)
        assertEquals(0, favoritesCount)

        migratedDb.close()
    }
}
