package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.lagnioledepapi.model.CocktailDetail
import com.supdevinci.lagnioledepapi.model.CocktailSource
import com.supdevinci.lagnioledepapi.model.favoriteKey
import com.supdevinci.lagnioledepapi.model.toDetail
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.FavoriteCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CocktailDetailUiState {
    val joke: String

    data class Loading(
        override val joke: String
    ) : CocktailDetailUiState

    data class Error(
        override val joke: String,
        val message: String
    ) : CocktailDetailUiState

    data class Success(
        override val joke: String,
        val cocktail: CocktailDetail,
        val isFavorite: Boolean
    ) : CocktailDetailUiState
}

sealed interface CocktailDetailEvent {
    data class ShareCocktail(val text: String) : CocktailDetailEvent
    data class CopyIngredients(val text: String) : CocktailDetailEvent
    data class ShowMessage(val text: String) : CocktailDetailEvent
}

class CocktailDetailViewModel(
    private val source: CocktailSource,
    private val id: String,
    private val cocktailRepository: CocktailRepository,
    private val customCocktailRepository: CustomCocktailRepository,
    private val favoriteCocktailRepository: FavoriteCocktailRepository,
    private val userStatsRepository: UserStatsRepository,
    jokeRepository: JokeRepository
) : ViewModel() {
    private val joke = jokeRepository.randomJoke()
    private val _uiState = MutableStateFlow<CocktailDetailUiState>(CocktailDetailUiState.Loading(joke))
    val uiState: StateFlow<CocktailDetailUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<CocktailDetailEvent>()
    val events: SharedFlow<CocktailDetailEvent> = _events.asSharedFlow()
    private var hasRecordedView = false

    init {
        loadCocktail()
    }

    private fun loadCocktail() {
        viewModelScope.launch {
            try {
                val detail = when (source) {
                    CocktailSource.REMOTE -> cocktailRepository.getCocktailById(id)?.toDetail()
                    CocktailSource.LOCAL -> id.toLongOrNull()
                        ?.let { customCocktailRepository.getCustomCocktail(it) }
                        ?.toDetail()
                }
                _uiState.value = if (detail != null) {
                    val isFavorite = favoriteCocktailRepository.isFavorite(detail.favoriteKey())
                    if (!hasRecordedView) {
                        userStatsRepository.recordDetailView()
                        hasRecordedView = true
                    }
                    CocktailDetailUiState.Success(
                        joke = joke,
                        cocktail = detail,
                        isFavorite = isFavorite
                    )
                } else {
                    CocktailDetailUiState.Error(
                        joke = joke,
                        message = "Le breuvage a disparu derrière le comptoir."
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.value = CocktailDetailUiState.Error(
                    joke = joke,
                    message = throwable.message ?: "Impossible d'ouvrir la fiche du cocktail."
                )
            }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value as? CocktailDetailUiState.Success ?: return
        viewModelScope.launch {
            val isFavorite = favoriteCocktailRepository.toggleFavorite(state.cocktail)
            _uiState.value = state.copy(isFavorite = isFavorite)
            _events.emit(
                CocktailDetailEvent.ShowMessage(
                    if (isFavorite) {
                        "Mis en favori, bien au chaud sur l'étagère. ❤️"
                    } else {
                        "Retiré des favoris, retour au fond du placard."
                    }
                )
            )
        }
    }

    fun shareCocktail() {
        val state = _uiState.value as? CocktailDetailUiState.Success ?: return
        viewModelScope.launch {
            userStatsRepository.recordShare()
            _events.emit(CocktailDetailEvent.ShareCocktail(buildShareText(state.cocktail)))
        }
    }

    fun copyIngredients() {
        val state = _uiState.value as? CocktailDetailUiState.Success ?: return
        viewModelScope.launch {
            userStatsRepository.recordCopy()
            _events.emit(CocktailDetailEvent.CopyIngredients(buildIngredientsText(state.cocktail)))
            _events.emit(CocktailDetailEvent.ShowMessage("Ingrédients copiés, plus d'excuse pour rater le mélange. 📋"))
        }
    }

    private fun buildShareText(detail: CocktailDetail): String = buildString {
        append(detail.name)
        append(" 🍸\n")
        append(detail.category)
        append(" • ")
        append(detail.badge)
        append("\n\nIngrédients:\n")
        append(buildIngredientsText(detail))
        append("\n\n")
        append(if (detail.source == CocktailSource.LOCAL) "Histoire" else "Instructions")
        append(":\n")
        append(detail.instructions)
    }

    private fun buildIngredientsText(detail: CocktailDetail): String =
        detail.ingredients.joinToString(separator = "\n") { ingredient ->
            val suffix = ingredient.dose.ifBlank { "Au pif" }
            "• ${ingredient.name} - $suffix"
        }
}
