package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.lagnioledepapi.model.CustomCocktail
import com.supdevinci.lagnioledepapi.model.CustomIngredient
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.CustomCocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateCocktailUiState(
    val joke: String,
    val name: String,
    val ingredientInput: String,
    val doseInput: String,
    val story: String,
    val ingredients: List<CustomIngredient>,
    val apiIngredients: List<String>,
    val savedCocktails: List<CustomCocktail>,
    val isLoadingIngredientCatalog: Boolean,
    val ingredientCatalogError: String?,
    val isSaving: Boolean,
    val errorMessage: String?
) {
    val suggestedIngredients: List<String>
        get() {
            val filtered = if (ingredientInput.isBlank()) {
                apiIngredients
            } else {
                apiIngredients.filter { ingredient ->
                    ingredient.contains(ingredientInput.trim(), ignoreCase = true)
                }
            }
            return filtered.take(12)
        }

    val canAddIngredient: Boolean
        get() = ingredientInput.isNotBlank() && doseInput.isNotBlank()

    val canSave: Boolean
        get() = name.isNotBlank() && ingredients.isNotEmpty() && !isSaving
}

class CreateCocktailViewModel(
    private val cocktailRepository: CocktailRepository,
    private val customCocktailRepository: CustomCocktailRepository,
    jokeRepository: JokeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        CreateCocktailUiState(
            joke = jokeRepository.randomJoke(),
            name = "",
            ingredientInput = "",
            doseInput = "",
            story = "",
            ingredients = emptyList(),
            apiIngredients = emptyList(),
            savedCocktails = emptyList(),
            isLoadingIngredientCatalog = true,
            ingredientCatalogError = null,
            isSaving = false,
            errorMessage = null
        )
    )
    val uiState: StateFlow<CreateCocktailUiState> = _uiState.asStateFlow()

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages: SharedFlow<String> = _snackbarMessages.asSharedFlow()

    init {
        viewModelScope.launch {
            customCocktailRepository.observeCustomCocktails().collect { cocktails ->
                _uiState.update { current -> current.copy(savedCocktails = cocktails) }
            }
        }
        loadIngredientCatalog()
    }

    private fun loadIngredientCatalog() {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    isLoadingIngredientCatalog = true,
                    ingredientCatalogError = null
                )
            }
            runCatching { cocktailRepository.getIngredientNames() }
                .onSuccess { ingredients ->
                    _uiState.update { current ->
                        current.copy(
                            apiIngredients = ingredients,
                            isLoadingIngredientCatalog = false,
                            ingredientCatalogError = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update { current ->
                        current.copy(
                            isLoadingIngredientCatalog = false,
                            ingredientCatalogError = "Le comptoir n'a pas réussi à charger les ingrédients de l'API."
                        )
                    }
                }
        }
    }

    fun updateName(value: String) {
        _uiState.update { current -> current.copy(name = value, errorMessage = null) }
    }

    fun updateIngredientInput(value: String) {
        _uiState.update { current -> current.copy(ingredientInput = value, errorMessage = null) }
    }

    fun updateDoseInput(value: String) {
        _uiState.update { current -> current.copy(doseInput = value, errorMessage = null) }
    }

    fun updateStory(value: String) {
        _uiState.update { current -> current.copy(story = value, errorMessage = null) }
    }

    fun selectSuggestedIngredient(value: String) {
        _uiState.update { current -> current.copy(ingredientInput = value, errorMessage = null) }
    }

    fun addIngredient() {
        val state = _uiState.value
        if (!state.canAddIngredient) {
            _uiState.update { current ->
                current.copy(errorMessage = "Mets un ingrédient et une dose, sinon c'est juste un concept.")
            }
            return
        }

        _uiState.update { current ->
            current.copy(
                ingredients = current.ingredients + CustomIngredient(
                    name = current.ingredientInput.trim(),
                    dose = current.doseInput.trim()
                ),
                ingredientInput = "",
                doseInput = "",
                errorMessage = null
            )
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update { current ->
            current.copy(
                ingredients = current.ingredients.filterIndexed { currentIndex, _ ->
                    currentIndex != index
                }
            )
        }
    }

    fun saveCocktail() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { current -> current.copy(errorMessage = "Le breuvage a besoin d'un nom, même foireux.") }
            return
        }
        if (state.ingredients.isEmpty()) {
            _uiState.update { current -> current.copy(errorMessage = "Ajoute au moins un ingrédient avant d'ouvrir le comptoir.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { current -> current.copy(isSaving = true, errorMessage = null) }
            customCocktailRepository.saveCustomCocktail(
                CustomCocktail(
                    name = state.name.trim(),
                    story = state.story.trim(),
                    ingredients = state.ingredients,
                    createdAt = System.currentTimeMillis()
                )
            )
            val currentJoke = _uiState.value.joke
            val apiIngredients = _uiState.value.apiIngredients
            val savedCocktails = _uiState.value.savedCocktails
            val isLoadingIngredientCatalog = _uiState.value.isLoadingIngredientCatalog
            val ingredientCatalogError = _uiState.value.ingredientCatalogError
            _uiState.value = CreateCocktailUiState(
                joke = currentJoke,
                name = "",
                ingredientInput = "",
                doseInput = "",
                story = "",
                ingredients = emptyList(),
                apiIngredients = apiIngredients,
                savedCocktails = savedCocktails,
                isLoadingIngredientCatalog = isLoadingIngredientCatalog,
                ingredientCatalogError = ingredientCatalogError,
                isSaving = false,
                errorMessage = null
            )
            _snackbarMessages.emit("Breuvage validé, rangé au frais et prêt à fanfaronner. 🍾")
        }
    }
}
