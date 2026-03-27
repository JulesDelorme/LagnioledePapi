package com.supdevinci.lagnioledepapi.model

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

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
                    dose = RemoteMeasureLocalizer.normalize(measure.orEmpty())
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

internal object RemoteMeasureLocalizer {
    private const val OUNCE_TO_CL = 2.957
    private val spacingRegex = Regex("\\s+")
    private val mixedNumberRegex = Regex("^(\\d+)[ -](\\d+)/(\\d+)$")
    private val fractionRegex = Regex("^(\\d+)/(\\d+)$")
    private val decimalRegex = Regex("^\\d+(?:[.,]\\d+)?$")
    private val amountUnitRegex = Regex(
        pattern = "^(?:(\\d+(?:[.,]\\d+)?(?:[ -]\\d+/\\d+)?|\\d+/\\d+)\\s+)?(oz\\.?|ounce|ounces|tsp\\.?|teaspoons?|tbsp\\.?|tblsp\\.?|tablespoons?|cups?|dash(?:es)?|splash(?:es)?|drops?|slices?|wedges?|sprigs?|leaf|leaves|cubes?|pinch|parts?)(?:\\b\\s*(.*))?$",
        options = setOf(RegexOption.IGNORE_CASE)
    )
    private val phraseTranslations = listOf(
        PhraseTranslation(source = "top up with", target = "Compléter avec"),
        PhraseTranslation(source = "top with", target = "Compléter avec"),
        PhraseTranslation(source = "fill with", target = "Compléter avec"),
        PhraseTranslation(source = "juice of", target = "Jus de"),
        PhraseTranslation(source = "twist of", target = "Zeste de")
    )
    private val unitTranslations = mapOf(
        "tsp" to UnitTranslation("c. à café"),
        "teaspoon" to UnitTranslation("c. à café"),
        "teaspoons" to UnitTranslation("c. à café"),
        "tbsp" to UnitTranslation("c. à soupe"),
        "tblsp" to UnitTranslation("c. à soupe"),
        "tablespoon" to UnitTranslation("c. à soupe"),
        "tablespoons" to UnitTranslation("c. à soupe"),
        "cup" to UnitTranslation("tasse", "tasses"),
        "cups" to UnitTranslation("tasse", "tasses"),
        "dash" to UnitTranslation("trait", "traits"),
        "dashes" to UnitTranslation("trait", "traits"),
        "splash" to UnitTranslation("trait", "traits"),
        "splashes" to UnitTranslation("trait", "traits"),
        "drop" to UnitTranslation("goutte", "gouttes"),
        "drops" to UnitTranslation("goutte", "gouttes"),
        "slice" to UnitTranslation("tranche", "tranches"),
        "slices" to UnitTranslation("tranche", "tranches"),
        "wedge" to UnitTranslation("quartier", "quartiers"),
        "wedges" to UnitTranslation("quartier", "quartiers"),
        "sprig" to UnitTranslation("brin", "brins"),
        "sprigs" to UnitTranslation("brin", "brins"),
        "leaf" to UnitTranslation("feuille", "feuilles"),
        "leaves" to UnitTranslation("feuille", "feuilles"),
        "cube" to UnitTranslation("cube", "cubes"),
        "cubes" to UnitTranslation("cube", "cubes"),
        "pinch" to UnitTranslation("pincée"),
        "part" to UnitTranslation("part", "parts"),
        "parts" to UnitTranslation("part", "parts")
    )

    fun normalize(measure: String): String {
        val normalized = measure.normalizeSpacing()
        if (normalized.isBlank()) return ""

        translatePhrase(normalized)?.let { return it }

        val match = amountUnitRegex.matchEntire(normalized) ?: return normalized
        val amountText = match.groupValues[1].ifBlank { null }
        val rawUnit = match.groupValues[2].normalizeUnitKey()
        val suffix = match.groupValues[3].normalizeSpacing()
        val amount = amountText?.let(::parseAmount) ?: if (amountText == null) null else return normalized

        if (rawUnit in ounceUnits) {
            if (amount == null) return normalized
            val roundedHalfCl = (amount * OUNCE_TO_CL * 2).roundToInt()
            val centiliters = formatHalfStepValue(roundedHalfCl)
            return appendSuffix("$centiliters cl", suffix)
        }

        val translation = unitTranslations[rawUnit] ?: return normalized
        val translatedUnit = if (isPlural(amount, rawUnit)) translation.plural else translation.singular
        val translatedAmount = amountText?.let(::normalizeAmountDisplay)
        val base = listOfNotNull(translatedAmount, translatedUnit).joinToString(" ")
        return appendSuffix(base, suffix)
    }

    private fun translatePhrase(measure: String): String? {
        val lowercase = measure.lowercase()
        val match = phraseTranslations.firstOrNull { translation ->
            lowercase == translation.source || lowercase.startsWith("${translation.source} ")
        } ?: return null
        val remainder = measure.drop(match.source.length).trim()
        return appendSuffix(match.target, remainder)
    }

    private fun parseAmount(amountText: String): Double? {
        val normalized = amountText.trim().replace(',', '.')
        mixedNumberRegex.matchEntire(normalized)?.let { match ->
            val whole = match.groupValues[1].toDouble()
            val numerator = match.groupValues[2].toDouble()
            val denominator = match.groupValues[3].toDouble()
            if (denominator == 0.0) return null
            return whole + (numerator / denominator)
        }
        fractionRegex.matchEntire(normalized)?.let { match ->
            val numerator = match.groupValues[1].toDouble()
            val denominator = match.groupValues[2].toDouble()
            if (denominator == 0.0) return null
            return numerator / denominator
        }
        return if (decimalRegex.matches(normalized)) normalized.toDoubleOrNull() else null
    }

    private fun normalizeAmountDisplay(amountText: String): String {
        val normalized = amountText.normalizeSpacing().replace(',', '.')
        mixedNumberRegex.matchEntire(normalized)?.let { match ->
            return "${match.groupValues[1]} ${match.groupValues[2]}/${match.groupValues[3]}"
        }
        return normalized.replace('.', ',')
    }

    private fun isPlural(amount: Double?, rawUnit: String): Boolean {
        if (amount != null) {
            return amount > 1.0
        }
        return rawUnit.endsWith("s") || rawUnit == "leaves"
    }

    private fun appendSuffix(base: String, suffix: String): String {
        if (suffix.isBlank()) return base
        return if (suffix.firstOrNull()?.isLetterOrDigit() == true) {
            "$base $suffix"
        } else {
            "$base$suffix"
        }
    }

    private fun formatHalfStepValue(halfSteps: Int): String {
        val whole = halfSteps / 2
        return if (halfSteps % 2 == 0) {
            whole.toString()
        } else {
            "$whole,5"
        }
    }

    private fun String.normalizeSpacing(): String = trim().replace(spacingRegex, " ")

    private fun String.normalizeUnitKey(): String = lowercase().removeSuffix(".")

    private data class UnitTranslation(
        val singular: String,
        val plural: String = singular
    )

    private data class PhraseTranslation(
        val source: String,
        val target: String
    )

    private val ounceUnits = setOf("oz", "ounce", "ounces")
}
