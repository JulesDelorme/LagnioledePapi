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
        assertEquals("4,5 cl", ingredients[0].dose)
        assertEquals("2 c. à café", ingredients[1].dose)
        assertEquals("Soda water", ingredients[2].name)
        assertEquals("trait", ingredients[2].dose)
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

    @Test
    fun normalizeRemoteMeasure_translatesOpeningExpression() {
        assertEquals(
            "Compléter avec soda water",
            RemoteMeasureLocalizer.normalize("Top up with soda water")
        )
    }

    @Test
    fun normalizeRemoteMeasure_keepsUnknownFormulaUntouched() {
        assertEquals(
            "Special blender magic",
            RemoteMeasureLocalizer.normalize("  Special   blender   magic ")
        )
    }

    @Test
    fun customCocktailToDetail_preservesLocalDoses() {
        val detail = CustomCocktail(
            id = 7L,
            name = "Le Local Brut",
            story = "Recette maison.",
            ingredients = listOf(CustomIngredient(name = "Rhum", dose = "1 1/2 oz")),
            createdAt = 1234L
        ).toDetail()

        assertEquals("1 1/2 oz", detail.ingredients.single().dose)
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
