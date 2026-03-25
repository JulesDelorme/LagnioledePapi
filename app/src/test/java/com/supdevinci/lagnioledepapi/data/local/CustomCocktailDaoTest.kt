package com.supdevinci.lagnioledepapi.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.supdevinci.lagnioledepapi.model.CustomIngredient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomCocktailDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: CustomCocktailDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.customCocktailDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadBack_preservesIngredients() = runTest {
        val cocktail = CustomCocktailEntity(
            name = "Le Barda du PMU",
            story = "Inventé après une belote musclée.",
            ingredientsJson = listOf(
                CustomIngredient("Rhum", "4 cl"),
                CustomIngredient("Citron", "1 trait")
            ),
            createdAt = 1234L
        )

        val insertedId = dao.insert(cocktail)
        val loaded = dao.getById(insertedId)
        val observed = dao.observeAll().first()

        assertNotNull(loaded)
        assertEquals("Le Barda du PMU", loaded?.name)
        assertEquals(2, loaded?.ingredientsJson?.size)
        assertEquals("Rhum", loaded?.ingredientsJson?.first()?.name)
        assertEquals(1, observed.size)
    }
}
