package com.supdevinci.lagnioledepapi.viewmodel

import androidx.lifecycle.ViewModel
import com.supdevinci.lagnioledepapi.data.BacCalculator
import com.supdevinci.lagnioledepapi.data.FakeData
import com.supdevinci.lagnioledepapi.model.BacDrinkSelection
import com.supdevinci.lagnioledepapi.model.BacResult
import com.supdevinci.lagnioledepapi.repository.JokeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BacUiState(
    val joke: String,
    val weightInput: String,
    val isMale: Boolean,
    val drinks: List<BacDrinkSelection>,
    val result: BacResult,
    val isSimulationVisible: Boolean
)

class BacTestViewModel(
    jokeRepository: JokeRepository
) : ViewModel() {
    private val initialDrinks = FakeData.drinkPresets.map { BacDrinkSelection(preset = it) }
    private val initialResult = BacCalculator.calculate(
        weightKg = 70.0,
        isMale = true,
        drinks = initialDrinks
    )

    private val _uiState = MutableStateFlow(
        BacUiState(
            joke = jokeRepository.randomJoke(),
            weightInput = "70",
            isMale = true,
            drinks = initialDrinks,
            result = initialResult,
            isSimulationVisible = false
        )
    )
    val uiState: StateFlow<BacUiState> = _uiState.asStateFlow()

    fun onWeightChange(input: String) {
        updateAndRecalculate { copy(weightInput = input.filter { it.isDigit() }) }
    }

    fun setGender(isMale: Boolean) {
        updateAndRecalculate { copy(isMale = isMale) }
    }

    fun incrementDrink(drinkId: String) {
        updateAndRecalculate {
            copy(
                drinks = drinks.map { selection ->
                    if (selection.preset.id == drinkId) {
                        selection.copy(quantity = selection.quantity + 1)
                    } else {
                        selection
                    }
                }
            )
        }
    }

    fun decrementDrink(drinkId: String) {
        updateAndRecalculate {
            copy(
                drinks = drinks.map { selection ->
                    if (selection.preset.id == drinkId) {
                        selection.copy(quantity = (selection.quantity - 1).coerceAtLeast(0))
                    } else {
                        selection
                    }
                }
            )
        }
    }

    fun resetDrinks() {
        updateAndRecalculate {
            copy(
                drinks = drinks.map { it.copy(quantity = 0) },
                isSimulationVisible = false
            )
        }
    }

    fun openSimulation() {
        _uiState.update { current ->
            if (current.result.totalPureAlcoholMl <= 0.0) current else current.copy(isSimulationVisible = true)
        }
    }

    fun dismissSimulation() {
        _uiState.update { current -> current.copy(isSimulationVisible = false) }
    }

    private fun updateAndRecalculate(transform: BacUiState.() -> BacUiState) {
        _uiState.update { current ->
            val updated = current.transform()
            updated.copy(
                result = BacCalculator.calculate(
                    weightKg = updated.weightInput.toDoubleOrNull() ?: 0.0,
                    isMale = updated.isMale,
                    drinks = updated.drinks
                )
            )
        }
    }
}
