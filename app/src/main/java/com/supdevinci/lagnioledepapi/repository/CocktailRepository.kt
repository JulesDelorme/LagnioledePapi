package com.supdevinci.lagnioledepapi.repository

import com.supdevinci.lagnioledepapi.model.Drink
import com.supdevinci.lagnioledepapi.service.CocktailService

class CocktailRepository(private val api: CocktailService) {
    suspend fun searchCocktails(query: String): List<Drink> =
        api.searchCocktails(query).drinks.orEmpty()

    suspend fun getCocktailsByLetter(letter: String): List<Drink> =
        api.listCocktailsByFirstLetter(letter).drinks.orEmpty()

    suspend fun getCocktailById(id: String): Drink? =
        api.getCocktailById(id).drinks.orEmpty().firstOrNull()

    suspend fun getRandomCocktail(): Drink? =
        api.getRandomCocktail().drinks.orEmpty().firstOrNull()

    suspend fun getIngredientNames(): List<String> =
        api.listIngredients().ingredients.orEmpty()
            .mapNotNull { ingredient -> ingredient.name?.trim() }
            .filter { ingredient -> ingredient.isNotBlank() }
            .distinct()
            .sorted()
}
