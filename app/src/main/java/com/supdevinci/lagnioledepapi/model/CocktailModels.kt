package com.supdevinci.lagnioledepapi.model

import com.google.gson.annotations.SerializedName

data class CocktailResponse(
    @SerializedName("drinks") val drinks: List<Drink>?
)

data class IngredientCatalogResponse(
    @SerializedName("drinks") val ingredients: List<IngredientCatalogItem>?
)

data class IngredientCatalogItem(
    @SerializedName("strIngredient1") val name: String?
)

data class Drink(
    @SerializedName("idDrink") val id: String,
    @SerializedName("strDrink") val name: String,
    @SerializedName("strDrinkThumb") val imageUrl: String,
    @SerializedName("strCategory") val category: String?,
    @SerializedName("strAlcoholic") val alcoholic: String?,
    @SerializedName("strInstructions") val instructions: String?,
    @SerializedName("strIngredient1") val strIngredient1: String?,
    @SerializedName("strIngredient2") val strIngredient2: String?,
    @SerializedName("strIngredient3") val strIngredient3: String?,
    @SerializedName("strIngredient4") val strIngredient4: String?,
    @SerializedName("strIngredient5") val strIngredient5: String?,
    @SerializedName("strIngredient6") val strIngredient6: String?,
    @SerializedName("strIngredient7") val strIngredient7: String?,
    @SerializedName("strIngredient8") val strIngredient8: String?,
    @SerializedName("strIngredient9") val strIngredient9: String?,
    @SerializedName("strIngredient10") val strIngredient10: String?,
    @SerializedName("strIngredient11") val strIngredient11: String?,
    @SerializedName("strIngredient12") val strIngredient12: String?,
    @SerializedName("strIngredient13") val strIngredient13: String?,
    @SerializedName("strIngredient14") val strIngredient14: String?,
    @SerializedName("strIngredient15") val strIngredient15: String?,
    @SerializedName("strMeasure1") val strMeasure1: String?,
    @SerializedName("strMeasure2") val strMeasure2: String?,
    @SerializedName("strMeasure3") val strMeasure3: String?,
    @SerializedName("strMeasure4") val strMeasure4: String?,
    @SerializedName("strMeasure5") val strMeasure5: String?,
    @SerializedName("strMeasure6") val strMeasure6: String?,
    @SerializedName("strMeasure7") val strMeasure7: String?,
    @SerializedName("strMeasure8") val strMeasure8: String?,
    @SerializedName("strMeasure9") val strMeasure9: String?,
    @SerializedName("strMeasure10") val strMeasure10: String?,
    @SerializedName("strMeasure11") val strMeasure11: String?,
    @SerializedName("strMeasure12") val strMeasure12: String?,
    @SerializedName("strMeasure13") val strMeasure13: String?,
    @SerializedName("strMeasure14") val strMeasure14: String?,
    @SerializedName("strMeasure15") val strMeasure15: String?
) {
    fun ingredientLines(): List<CustomIngredient> {
        val ingredients = listOf(
            strIngredient1,
            strIngredient2,
            strIngredient3,
            strIngredient4,
            strIngredient5,
            strIngredient6,
            strIngredient7,
            strIngredient8,
            strIngredient9,
            strIngredient10,
            strIngredient11,
            strIngredient12,
            strIngredient13,
            strIngredient14,
            strIngredient15
        )
        val measures = listOf(
            strMeasure1,
            strMeasure2,
            strMeasure3,
            strMeasure4,
            strMeasure5,
            strMeasure6,
            strMeasure7,
            strMeasure8,
            strMeasure9,
            strMeasure10,
            strMeasure11,
            strMeasure12,
            strMeasure13,
            strMeasure14,
            strMeasure15
        )

        return ingredients.zip(measures).mapNotNull { (ingredient, measure) ->
            val trimmedIngredient = ingredient?.trim().orEmpty()
            if (trimmedIngredient.isBlank()) {
                null
            } else {
                CustomIngredient(
                    name = trimmedIngredient,
                    dose = measure?.trim().orEmpty()
                )
            }
        }
    }

    fun shortIngredients(limit: Int = 3): String = ingredientLines()
        .take(limit)
        .joinToString(", ") { ingredient ->
            if (ingredient.dose.isBlank()) ingredient.name else "${ingredient.name} ${ingredient.dose}"
        }

    fun toDetail(): CocktailDetail = CocktailDetail(
        id = id,
        source = CocktailSource.REMOTE,
        name = name,
        category = category ?: "Apéro surprise",
        imageUrl = imageUrl,
        ingredients = ingredientLines(),
        instructions = instructions ?: "Pas d'instructions, ça se boit à l'instinct.",
        badge = alcoholic ?: "Classique",
        accent = "Recette du zinc"
    )
}
