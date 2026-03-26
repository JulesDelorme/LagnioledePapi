package com.supdevinci.lagnioledepapi.viewmodel

import com.supdevinci.lagnioledepapi.data.local.CustomCocktailEntity
import com.supdevinci.lagnioledepapi.data.local.FavoriteCocktailEntity
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.model.CustomIngredient
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.testutil.FakeCustomCocktailDao
import com.supdevinci.lagnioledepapi.testutil.FakeFavoriteCocktailDao
import com.supdevinci.lagnioledepapi.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CellarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun blankSearch_exposesFavoritesAndCustomCocktails() = runTest {
        val viewModel = CellarViewModel(
            favoriteCocktailRepository = FavoriteCocktailRepository(
                FakeFavoriteCocktailDao(
                    listOf(
                        favoriteEntity(
                            key = "remote:11000",
                            source = CocktailSource.REMOTE,
                            id = "11000",
                            name = "Mojito",
                            savedAt = 10L
                        ),
                        favoriteEntity(
                            key = "remote:11001",
                            source = CocktailSource.REMOTE,
                            id = "11001",
                            name = "Negroni",
                            savedAt = 20L
                        )
                    )
                )
            ),
            customCocktailRepository = CustomCocktailRepository(
                FakeCustomCocktailDao(
                    listOf(
                        customEntity(
                            id = 8L,
                            name = "La Grosse Tisane",
                            story = "Inventee au comptoir.",
                            ingredientName = "Citron"
                        )
                    )
                )
            ),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(2, state.filteredFavoriteCocktails.size)
        assertEquals(1, state.filteredCustomCocktails.size)
        assertEquals("Negroni", state.filteredFavoriteCocktails.first().name)
        assertEquals("La Grosse Tisane", state.filteredCustomCocktails.first().name)

        collection.cancel()
    }

    @Test
    fun searchQuery_filtersFavoritesAndCustomCocktails() = runTest {
        val viewModel = CellarViewModel(
            favoriteCocktailRepository = FavoriteCocktailRepository(
                FakeFavoriteCocktailDao(
                    listOf(
                        favoriteEntity(
                            key = "remote:11000",
                            source = CocktailSource.REMOTE,
                            id = "11000",
                            name = "Mojito",
                            category = "Long Drink"
                        ),
                        favoriteEntity(
                            key = "local:7",
                            source = CocktailSource.LOCAL,
                            id = "7",
                            name = "Brouet Maison",
                            category = "Creation du comptoir"
                        )
                    )
                )
            ),
            customCocktailRepository = CustomCocktailRepository(
                FakeCustomCocktailDao(
                    listOf(
                        customEntity(
                            id = 12L,
                            name = "Potion du Zinc",
                            story = "Avec un peu de citron pour reveiller les morts.",
                            ingredientName = "Citron"
                        ),
                        customEntity(
                            id = 13L,
                            name = "Velours du PMU",
                            story = "Plus doux.",
                            ingredientName = "Rhum"
                        )
                    )
                )
            ),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()
        viewModel.onSearchQueryChange("citron")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state.filteredFavoriteCocktails.isEmpty())
        assertEquals(1, state.filteredCustomCocktails.size)
        assertEquals("Potion du Zinc", state.filteredCustomCocktails.first().name)

        collection.cancel()
    }

    private fun favoriteEntity(
        key: String,
        source: CocktailSource,
        id: String,
        name: String,
        category: String = "Classique",
        badge: String = "Alcoholic",
        savedAt: Long = 1L
    ): FavoriteCocktailEntity = FavoriteCocktailEntity(
        favoriteKey = key,
        source = source.name.lowercase(),
        remoteIdOrLocalId = id,
        name = name,
        imageUrl = "https://example.com/$id.jpg",
        category = category,
        badge = badge,
        savedAt = savedAt
    )

    private fun customEntity(
        id: Long,
        name: String,
        story: String,
        ingredientName: String
    ): CustomCocktailEntity = CustomCocktailEntity(
        id = id,
        name = name,
        story = story,
        ingredientsJson = listOf(CustomIngredient(name = ingredientName, dose = "4 cl")),
        createdAt = id
    )
}
