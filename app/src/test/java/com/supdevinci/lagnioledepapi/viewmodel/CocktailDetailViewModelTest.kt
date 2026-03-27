package com.supdevinci.lagnioledepapi.viewmodel

import com.supdevinci.lagnioledepapi.model.CocktailResponse
import com.supdevinci.lagnioledepapi.model.Drink
import com.supdevinci.lagnioledepapi.model.IngredientCatalogResponse
import com.supdevinci.lagnioledepapi.model.favoriteKey
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import com.supdevinci.lagnioledepapi.service.CocktailService
import com.supdevinci.lagnioledepapi.testutil.FakeCustomCocktailDao
import com.supdevinci.lagnioledepapi.testutil.FakeFavoriteCocktailDao
import com.supdevinci.lagnioledepapi.testutil.FakeUserStatsDao
import com.supdevinci.lagnioledepapi.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CocktailDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadRemoteCocktail_recordsViewAndTogglesFavorite() = runTest {
        val favoriteRepository = FavoriteCocktailRepository(FakeFavoriteCocktailDao())
        val userStatsRepository = UserStatsRepository(FakeUserStatsDao())
        val viewModel = CocktailDetailViewModel(
            source = com.supdevinci.lagnioledepapi.model.CocktailSource.REMOTE,
            id = "42",
            cocktailRepository = CocktailRepository(FakeCocktailService()),
            customCocktailRepository = CustomCocktailRepository(FakeCustomCocktailDao()),
            favoriteCocktailRepository = favoriteRepository,
            userStatsRepository = userStatsRepository,
            jokeRepository = JokeRepository()
        )

        val beforeToggle = viewModel.uiState.first { it is CocktailDetailUiState.Success } as CocktailDetailUiState.Success
        viewModel.toggleFavorite()
        val afterToggle = viewModel.uiState.first {
            (it as? CocktailDetailUiState.Success)?.isFavorite == true
        } as CocktailDetailUiState.Success
        val stats = userStatsRepository.observeStats().first()

        assertTrue(!beforeToggle.isFavorite)
        assertTrue(afterToggle.isFavorite)
        assertEquals(1, stats.detailViews)
        assertTrue(favoriteRepository.isFavorite(afterToggle.cocktail.favoriteKey()))
    }

    @Test
    fun shareAndCopy_emitEventsAndRecordStats() = runTest {
        val userStatsRepository = UserStatsRepository(FakeUserStatsDao())
        val viewModel = CocktailDetailViewModel(
            source = com.supdevinci.lagnioledepapi.model.CocktailSource.REMOTE,
            id = "42",
            cocktailRepository = CocktailRepository(FakeCocktailService()),
            customCocktailRepository = CustomCocktailRepository(FakeCustomCocktailDao()),
            favoriteCocktailRepository = FavoriteCocktailRepository(FakeFavoriteCocktailDao()),
            userStatsRepository = userStatsRepository,
            jokeRepository = JokeRepository()
        )

        viewModel.uiState.first { it is CocktailDetailUiState.Success }
        val shareEvent = async { viewModel.events.first { it is CocktailDetailEvent.ShareCocktail } }
        viewModel.shareCocktail()
        val share = shareEvent.await() as CocktailDetailEvent.ShareCocktail

        val copyEvent = async { viewModel.events.first { it is CocktailDetailEvent.CopyIngredients } }
        viewModel.copyIngredients()
        val copy = copyEvent.await() as CocktailDetailEvent.CopyIngredients

        val stats = userStatsRepository.observeStats().first()

        assertTrue(share.text.contains("Margarita du Beaujolais"))
        assertTrue(share.text.contains("• Tequila - 4,5 cl"))
        assertTrue(share.text.contains("• Triple sec - 2 c. à café"))
        assertTrue(copy.text.contains("• Tequila - 4,5 cl"))
        assertTrue(copy.text.contains("• Soda water - trait"))
        assertEquals(1, stats.shareActions)
        assertEquals(1, stats.copyActions)
    }

    private class FakeCocktailService : CocktailService {
        override suspend fun searchCocktails(query: String): CocktailResponse = CocktailResponse(emptyList())

        override suspend fun listCocktailsByFirstLetter(letter: String): CocktailResponse = CocktailResponse(emptyList())

        override suspend fun getCocktailById(id: String): CocktailResponse =
            CocktailResponse(listOf(sampleDrink))

        override suspend fun getRandomCocktail(): CocktailResponse = CocktailResponse(emptyList())

        override suspend fun listIngredients(filter: String): IngredientCatalogResponse =
            IngredientCatalogResponse(emptyList())
    }

    companion object {
        private val sampleDrink = Drink(
            id = "42",
            name = "Margarita du Beaujolais",
            imageUrl = "https://example.com/margarita.jpg",
            category = "Classique",
            alcoholic = "Alcoholic",
            instructions = "Tout secouer et servir frais.",
            strIngredient1 = "Tequila",
            strIngredient2 = "Triple sec",
            strIngredient3 = "Soda water",
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
            strMeasure1 = "1 1/2 oz",
            strMeasure2 = "2 tsp",
            strMeasure3 = "Dash",
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
}
