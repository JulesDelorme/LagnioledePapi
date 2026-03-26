package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import com.supdevinci.lagnioledepapi.model.FavoriteCocktailSummary
import com.supdevinci.lagnioledepapi.model.matchesQuery
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CellarUiState(
    val joke: String,
    val searchQuery: String,
    val favoriteCocktails: List<FavoriteCocktailSummary>,
    val customCocktails: List<CustomCocktail>
) {
    val filteredFavoriteCocktails: List<FavoriteCocktailSummary>
        get() = favoriteCocktails.filter { favorite -> favorite.matchesQuery(searchQuery) }

    val filteredCustomCocktails: List<CustomCocktail>
        get() = customCocktails.filter { cocktail -> cocktail.matchesQuery(searchQuery) }

    val isCellarEmpty: Boolean
        get() = favoriteCocktails.isEmpty() && customCocktails.isEmpty()

    val hasSearchResults: Boolean
        get() = filteredFavoriteCocktails.isNotEmpty() || filteredCustomCocktails.isNotEmpty()
}

class CellarViewModel(
    favoriteCocktailRepository: FavoriteCocktailRepository,
    customCocktailRepository: CustomCocktailRepository,
    jokeRepository: JokeRepository
) : ViewModel() {
    private val joke = jokeRepository.randomJoke()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<CellarUiState> = combine(
        searchQuery,
        favoriteCocktailRepository.observeFavoriteCocktails(),
        customCocktailRepository.observeCustomCocktails()
    ) { query, favorites, customCocktails ->
        CellarUiState(
            joke = joke,
            searchQuery = query,
            favoriteCocktails = favorites,
            customCocktails = customCocktails
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CellarUiState(
            joke = joke,
            searchQuery = "",
            favoriteCocktails = emptyList(),
            customCocktails = emptyList()
        )
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
