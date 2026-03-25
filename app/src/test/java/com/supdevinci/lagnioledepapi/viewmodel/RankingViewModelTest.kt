package com.supdevinci.lagnioledepapi.viewmodel

import com.supdevinci.lagnioledepapi.data.local.CustomCocktailEntity
import com.supdevinci.lagnioledepapi.data.local.FavoriteCocktailEntity
import com.supdevinci.lagnioledepapi.data.local.UserStatsEntity
import com.supdevinci.lagnioledepapi.model.CustomIngredient
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.RankingRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import com.supdevinci.lagnioledepapi.testutil.FakeCustomCocktailDao
import com.supdevinci.lagnioledepapi.testutil.FakeFavoriteCocktailDao
import com.supdevinci.lagnioledepapi.testutil.FakeUserStatsDao
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
class RankingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun ranking_injectsLocalProfileWithStatsSummary() = runTest {
        val viewModel = RankingViewModel(
            rankingRepository = RankingRepository(),
            favoriteCocktailRepository = FavoriteCocktailRepository(
                FakeFavoriteCocktailDao(
                    initialItems = listOf(
                        FavoriteCocktailEntity(
                            favoriteKey = "remote:1",
                            source = "remote",
                            remoteIdOrLocalId = "1",
                            name = "Mojito",
                            imageUrl = null,
                            category = "Cocktail",
                            badge = "Alcoholic",
                            savedAt = 1L
                        )
                    )
                )
            ),
            customCocktailRepository = CustomCocktailRepository(
                FakeCustomCocktailDao(
                    initialItems = listOf(
                        CustomCocktailEntity(
                            id = 1L,
                            name = "Maison",
                            story = "Au PMU",
                            ingredientsJson = listOf(CustomIngredient("Gin", "4 cl")),
                            createdAt = 1L
                        )
                    )
                )
            ),
            userStatsRepository = UserStatsRepository(
                FakeUserStatsDao(
                    UserStatsEntity(
                        detailViews = 12,
                        surpriseOpens = 3,
                        copyActions = 2,
                        shareActions = 1
                    )
                )
            ),
            jokeRepository = JokeRepository()
        )

        val collection = backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()
        val localEntry = viewModel.uiState.value.rankings.first { it.isLocalProfile }

        assertEquals("pts", localEntry.scoreLabel)
        assertTrue(localEntry.statsSummary!!.contains("12 fiches vues"))
        assertTrue(localEntry.score > 0)
        collection.cancel()
    }
}
