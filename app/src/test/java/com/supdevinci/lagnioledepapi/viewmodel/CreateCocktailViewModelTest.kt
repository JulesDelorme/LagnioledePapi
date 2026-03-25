package com.supdevinci.lagnioledepapi.viewmodel

import com.supdevinci.lagnioledepapi.model.CocktailResponse
import com.supdevinci.lagnioledepapi.model.IngredientCatalogItem
import com.supdevinci.lagnioledepapi.model.IngredientCatalogResponse
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.service.CocktailService
import com.supdevinci.lagnioledepapi.testutil.FakeCustomCocktailDao
import com.supdevinci.lagnioledepapi.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateCocktailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun saveCocktail_persistsLocalCocktailAndResetsForm() = runTest {
        val dao = FakeCustomCocktailDao()
        val repository = CustomCocktailRepository(dao)
        val viewModel = CreateCocktailViewModel(
            cocktailRepository = CocktailRepository(
                FakeCocktailService(listOf("Gin", "Rhum blanc", "Tequila"))
            ),
            customCocktailRepository = repository,
            jokeRepository = JokeRepository()
        )
        val snackbarMessage = async { viewModel.snackbarMessages.first() }
        advanceUntilIdle()

        viewModel.updateName("La Cuite de Dédé")
        viewModel.updateIngredientInput("Rhum blanc")
        viewModel.updateDoseInput("4 cl")
        viewModel.addIngredient()
        viewModel.updateStory("Testé après deux parties de fléchettes.")
        viewModel.saveCocktail()

        val savedCocktails = repository.observeCustomCocktails().first { it.isNotEmpty() }
        val state = viewModel.uiState.first { current ->
            current.name.isBlank() && current.ingredients.isEmpty() && !current.isSaving
        }

        assertEquals(1, savedCocktails.size)
        assertEquals("La Cuite de Dédé", savedCocktails.first().name)
        assertEquals(1, state.savedCocktails.size)
        assertTrue(snackbarMessage.await().contains("Breuvage validé"))
        assertEquals("", state.name)
        assertTrue(state.ingredients.isEmpty())
    }

    @Test
    fun ingredientSuggestions_areLoadedFromApiAndFilteredByInput() = runTest {
        val viewModel = CreateCocktailViewModel(
            cocktailRepository = CocktailRepository(
                FakeCocktailService(listOf("Vodka", "Dark rum", "White rum", "Gin"))
            ),
            customCocktailRepository = CustomCocktailRepository(FakeCustomCocktailDao()),
            jokeRepository = JokeRepository()
        )

        advanceUntilIdle()
        viewModel.updateIngredientInput("rum")

        val state = viewModel.uiState.value

        assertEquals(listOf("Dark rum", "White rum"), state.suggestedIngredients)
        assertEquals(4, state.apiIngredients.size)
        assertTrue(state.ingredientCatalogError == null)
        assertTrue(!state.isLoadingIngredientCatalog)
    }

    private class FakeCocktailService(
        ingredientNames: List<String>
    ) : CocktailService {
        private val ingredientResponse = IngredientCatalogResponse(
            ingredients = ingredientNames.map { name -> IngredientCatalogItem(name = name) }
        )

        override suspend fun searchCocktails(query: String): CocktailResponse =
            CocktailResponse(emptyList())

        override suspend fun listCocktailsByFirstLetter(letter: String): CocktailResponse =
            CocktailResponse(emptyList())

        override suspend fun getCocktailById(id: String): CocktailResponse =
            CocktailResponse(emptyList())

        override suspend fun getRandomCocktail(): CocktailResponse =
            CocktailResponse(emptyList())

        override suspend fun listIngredients(filter: String): IngredientCatalogResponse =
            ingredientResponse
    }
}
