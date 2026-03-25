package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.model.RankingEntry
import com.supdevinci.lagnioledepapi.model.RankingRegion
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.RankingRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class RankingUiState(
    val joke: String,
    val selectedRegion: RankingRegion,
    val rankings: List<RankingEntry>
)

class RankingViewModel(
    private val rankingRepository: RankingRepository,
    favoriteCocktailRepository: FavoriteCocktailRepository,
    customCocktailRepository: CustomCocktailRepository,
    userStatsRepository: UserStatsRepository,
    jokeRepository: JokeRepository
) : ViewModel() {
    private val joke = jokeRepository.randomJoke()
    private val selectedRegion = MutableStateFlow(RankingRegion.MONDIAL)

    val uiState: StateFlow<RankingUiState> = combine(
        selectedRegion,
        favoriteCocktailRepository.observeFavoritesCount(),
        customCocktailRepository.observeCount(),
        userStatsRepository.observeStats()
    ) { region, favoritesCount, customCocktailsCount, userStats ->
        RankingUiState(
            joke = joke,
            selectedRegion = region,
            rankings = rankingRepository.getHybridRankings(
                region = region,
                userStats = userStats,
                favoritesCount = favoritesCount,
                customCocktailsCount = customCocktailsCount
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RankingUiState(
            joke = joke,
            selectedRegion = selectedRegion.value,
            rankings = rankingRepository.getHybridRankings(
                region = selectedRegion.value,
                userStats = com.supdevinci.lagnioledepapi.model.UserStats(),
                favoritesCount = 0,
                customCocktailsCount = 0
            )
        )
    )

    fun onRegionSelected(region: RankingRegion) {
        selectedRegion.value = region
    }
}
