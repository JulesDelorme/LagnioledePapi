package com.supdevinci.lagnioledepapi.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkMappingTest {
    @Test
    fun ingredientLines_extractsNamesAndMeasures() {
        val drink = sampleDrink()

        val ingredients = drink.ingredientLines()

        assertEquals(3, ingredients.size)
        assertEquals("Tequila", ingredients[0].name)
        assertEquals("2 cl", ingredients[0].dose)
        assertEquals("Citron", ingredients[2].name)
    }

    @Test
    fun toDetail_mapsRemoteDrinkToUnifiedDetail() {
        val detail = sampleDrink().toDetail()

        assertEquals(CocktailSource.REMOTE, detail.source)
        assertEquals("Classique", detail.category)
        assertEquals("Alcoholic", detail.badge)
        assertTrue(detail.instructions.contains("secouer"))
    }

    @Test
    fun nullDrinkList_canBeConsumedAsEmpty() {
        val response = CocktailResponse(drinks = null)

        assertTrue(response.drinks.orEmpty().isEmpty())
    }

    private fun sampleDrink(): Drink = Drink(
        id = "42",
        name = "Margarita du Beaujolais",
        imageUrl = "https://example.com/margarita.jpg",
        category = "Classique",
        alcoholic = "Alcoholic",
        instructions = "Tout secouer et servir frais.",
        strIngredient1 = "Tequila",
        strIngredient2 = "Triple sec",
        strIngredient3 = "Citron",
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
        strMeasure1 = "2 cl",
        strMeasure2 = "1 cl",
        strMeasure3 = "1 trait",
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
