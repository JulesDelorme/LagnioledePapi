package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.lagnioledepapi.model.Drink
import com.supdevinci.lagnioledepapi.repository.CocktailRepository
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import com.supdevinci.lagnioledepapi.repository.UserStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

sealed interface CocktailListUiState {
    val joke: String
    val surpriseCocktail: Drink?
    val isSurpriseLoading: Boolean
    val surpriseErrorMessage: String?

    data class Loading(
        override val joke: String,
        override val surpriseCocktail: Drink?,
        override val isSurpriseLoading: Boolean,
        override val surpriseErrorMessage: String?
    ) : CocktailListUiState

    data class Error(
        override val joke: String,
        override val surpriseCocktail: Drink?,
        override val isSurpriseLoading: Boolean,
        override val surpriseErrorMessage: String?,
        val message: String
    ) : CocktailListUiState

    data class Success(
        override val joke: String,
        override val surpriseCocktail: Drink?,
        override val isSurpriseLoading: Boolean,
        override val surpriseErrorMessage: String?,
        val remoteCocktails: List<Drink>
    ) : CocktailListUiState
}

private sealed interface RemoteCocktailState {
    data object Loading : RemoteCocktailState
    data class Error(val message: String) : RemoteCocktailState
    data class Success(val cocktails: List<Drink>) : RemoteCocktailState
}

private sealed interface SurpriseCocktailState {
    data object Hidden : SurpriseCocktailState
    data object Loading : SurpriseCocktailState
    data class Error(val message: String) : SurpriseCocktailState
    data class Success(val cocktail: Drink) : SurpriseCocktailState
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CocktailListViewModel(
    private val cocktailRepository: CocktailRepository,
    private val userStatsRepository: UserStatsRepository,
    jokeRepository: JokeRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val joke = MutableStateFlow(jokeRepository.randomJoke())
    private val surpriseRefresh = MutableStateFlow(0)

    private val remoteCocktails = searchQuery
        .debounce(350)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                emit(RemoteCocktailState.Loading)
                val cocktails = if (query.isBlank()) {
                    cocktailRepository.getCocktailsByLetter("a")
                } else {
                    cocktailRepository.searchCocktails(query.trim())
                }
                emit(RemoteCocktailState.Success(cocktails))
            }.catch { throwable ->
                emit(RemoteCocktailState.Error(throwable.message ?: "Le zinc a fermé sans prévenir."))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RemoteCocktailState.Loading
        )

    private val surpriseCocktailState = combine(searchQuery, surpriseRefresh) { query, refresh ->
        query.trim() to refresh
    }.flatMapLatest { (query, _) ->
        if (query.isNotBlank()) {
            flow { emit(SurpriseCocktailState.Hidden) }
        } else {
            flow {
                emit(SurpriseCocktailState.Loading)
                val cocktail = cocktailRepository.getRandomCocktail()
                if (cocktail != null) {
                    emit(SurpriseCocktailState.Success(cocktail))
                } else {
                    emit(SurpriseCocktailState.Error("Dédé n'a pas trouvé de surprise à servir."))
                }
            }.catch { throwable ->
                emit(
                    SurpriseCocktailState.Error(
                        throwable.message ?: "La surprise s'est renversée avant d'arriver."
                    )
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SurpriseCocktailState.Loading
    )

    val uiState: StateFlow<CocktailListUiState> = combine(
        joke,
        remoteCocktails,
        surpriseCocktailState
    ) { currentJoke, remote, surprise ->
        val surpriseCocktail = (surprise as? SurpriseCocktailState.Success)?.cocktail
        val isSurpriseLoading = surprise is SurpriseCocktailState.Loading
        val surpriseErrorMessage = (surprise as? SurpriseCocktailState.Error)?.message

        when (remote) {
            RemoteCocktailState.Loading -> CocktailListUiState.Loading(
                joke = currentJoke,
                surpriseCocktail = surpriseCocktail,
                isSurpriseLoading = isSurpriseLoading,
                surpriseErrorMessage = surpriseErrorMessage
            )

            is RemoteCocktailState.Error -> CocktailListUiState.Error(
                joke = currentJoke,
                surpriseCocktail = surpriseCocktail,
                isSurpriseLoading = isSurpriseLoading,
                surpriseErrorMessage = surpriseErrorMessage,
                message = remote.message
            )

            is RemoteCocktailState.Success -> CocktailListUiState.Success(
                joke = currentJoke,
                surpriseCocktail = surpriseCocktail,
                isSurpriseLoading = isSurpriseLoading,
                surpriseErrorMessage = surpriseErrorMessage,
                remoteCocktails = remote.cocktails
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CocktailListUiState.Loading(
            joke = joke.value,
            surpriseCocktail = null,
            isSurpriseLoading = true,
            surpriseErrorMessage = null
        )
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refreshSurpriseCocktail() {
        if (_searchQuery.value.isBlank()) {
            surpriseRefresh.value += 1
        }
    }

    fun onSurpriseOpened() {
        viewModelScope.launch {
            userStatsRepository.recordSurpriseOpen()
        }
    }
}
