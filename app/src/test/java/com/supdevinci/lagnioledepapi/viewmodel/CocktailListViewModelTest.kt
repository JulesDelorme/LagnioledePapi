package com.supdevinci.lagnioledepapi.viewmodel

import com.supdevinci.lagnioledepapi.model.CocktailResponse
import com.supdevinci.lagnioledepapi.model.Drink
import com.supdevinci.lagnioledepapi.model.IngredientCatalogResponse
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import com.supdevinci.lagnioledepapi.service.CocktailService
import com.supdevinci.lagnioledepapi.testutil.FakeUserStatsDao
import com.supdevinci.lagnioledepapi.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CocktailListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun emptySearch_loadsCocktailsByLetter() = runTest {
        val service = FakeCocktailService(
            letterDrinks = listOf(drink(id = "1", name = "Americano")),
            searchDrinks = emptyList(),
            randomDrink = drink(id = "77", name = "Surprise")
        )
        val viewModel = CocktailListViewModel(
            cocktailRepository = CocktailRepository(service),
            userStatsRepository = UserStatsRepository(FakeUserStatsDao()),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        advanceTimeBy(400)
        advanceUntilIdle()

        val state = viewModel.uiState.value as CocktailListUiState.Success
        assertEquals(1, service.letterCalls)
        assertEquals(0, service.searchCalls.size)
        assertEquals("Americano", state.remoteCocktails.first().name)
        assertEquals("Surprise", state.surpriseCocktail?.name)

        collection.cancel()
    }

    @Test
    fun searchQuery_usesSearchEndpoint() = runTest {
        val service = FakeCocktailService(
            letterDrinks = emptyList(),
            searchDrinks = listOf(drink(id = "2", name = "Mojito")),
            randomDrink = drink(id = "55", name = "Negroni")
        )
        val viewModel = CocktailListViewModel(
            cocktailRepository = CocktailRepository(service),
            userStatsRepository = UserStatsRepository(FakeUserStatsDao()),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        viewModel.onSearchQueryChange("mojito")
        advanceTimeBy(400)
        advanceUntilIdle()

        val state = viewModel.uiState.value as CocktailListUiState.Success
        assertEquals(listOf("mojito"), service.searchCalls)
        assertEquals("Mojito", state.remoteCocktails.first().name)
        assertEquals(null, state.surpriseCocktail)

        collection.cancel()
    }

    @Test
    fun refreshSurprise_loadsAnotherRandomCocktail() = runTest {
        val service = FakeCocktailService(
            letterDrinks = listOf(drink(id = "1", name = "Americano")),
            searchDrinks = emptyList(),
            randomDrink = drink(id = "55", name = "First"),
            extraRandomDrinks = listOf(drink(id = "56", name = "Second"))
        )
        val viewModel = CocktailListViewModel(
            cocktailRepository = CocktailRepository(service),
            userStatsRepository = UserStatsRepository(FakeUserStatsDao()),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        advanceTimeBy(400)
        advanceUntilIdle()
        viewModel.refreshSurpriseCocktail()
        advanceUntilIdle()

        val state = viewModel.uiState.value as CocktailListUiState.Success
        assertEquals(2, service.randomCalls)
        assertEquals("Second", state.surpriseCocktail?.name)

        collection.cancel()
    }

    private class FakeCocktailService(
        private val letterDrinks: List<Drink>,
        private val searchDrinks: List<Drink>,
        private val randomDrink: Drink,
        private val extraRandomDrinks: List<Drink> = emptyList()
    ) : CocktailService {
        var letterCalls: Int = 0
        val searchCalls = mutableListOf<String>()
        var randomCalls: Int = 0

        override suspend fun searchCocktails(query: String): CocktailResponse {
            searchCalls += query
            return CocktailResponse(searchDrinks)
        }

        override suspend fun listCocktailsByFirstLetter(letter: String): CocktailResponse {
            letterCalls += 1
            return CocktailResponse(letterDrinks)
        }

        override suspend fun getCocktailById(id: String): CocktailResponse = CocktailResponse(emptyList())

        override suspend fun getRandomCocktail(): CocktailResponse {
            val drink = if (randomCalls == 0) {
                randomDrink
            } else {
                extraRandomDrinks.getOrElse(randomCalls - 1) { randomDrink }
            }
            randomCalls += 1
            return CocktailResponse(listOf(drink))
        }

        override suspend fun listIngredients(filter: String): IngredientCatalogResponse =
            IngredientCatalogResponse(emptyList())
    }

    private fun drink(id: String, name: String): Drink = Drink(
        id = id,
        name = name,
        imageUrl = "https://example.com/$id.jpg",
        category = "Classique",
        alcoholic = "Alcoholic",
        instructions = "Mélange ça bien.",
        strIngredient1 = "Citron",
        strIngredient2 = "Glace",
        strIngredient3 = null,
        strIngredient4 = null,
        strIngredient5 = null,
        strIngredient6 = null,
        strIngredient7 = null,
        strIngredient8 = null,
        strIngredient9 = null,
        strIngredient10 = null,
        strIngredient11 = null,
        strIngredient12 = null,
        strIngredient13 = null,
        strIngredient14 = null,
        strIngredient15 = null,
        strMeasure1 = "1 trait",
        strMeasure2 = "2 glaçons",
        strMeasure3 = null,
        strMeasure4 = null,
        strMeasure5 = null,
        strMeasure6 = null,
        strMeasure7 = null,
        strMeasure8 = null,
        strMeasure9 = null,
        strMeasure10 = null,
        strMeasure11 = null,
        strMeasure12 = null,
        strMeasure13 = null,
        strMeasure14 = null,
        strMeasure15 = null
    )
}
