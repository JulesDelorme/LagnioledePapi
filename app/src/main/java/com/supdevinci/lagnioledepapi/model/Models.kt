package com.supdevinci.lagnioledepapi.model

enum class CocktailSource {
    REMOTE,
    LOCAL
}

data class CustomIngredient(
    val name: String,
    val dose: String
)

data class CustomCocktail(
    val id: Long = 0L,
    val name: String,
    val story: String,
    val ingredients: List<CustomIngredient>,
    val createdAt: Long
)

data class CocktailDetail(
    val id: String,
    val source: CocktailSource,
    val name: String,
    val category: String,
    val imageUrl: String?,
    val ingredients: List<CustomIngredient>,
    val instructions: String,
    val badge: String,
    val accent: String
)

fun CocktailDetail.favoriteKey(): String = "${source.name.lowercase()}:$id"

enum class RankingRegion {
    MONDIAL,
    EUROPEEN,
    FRANCAIS
}

data class RankingEntry(
    val id: Int,
    val name: String,
    val score: Int,
    val badge: String,
    val region: RankingRegion,
    val flag: String,
    val scoreLabel: String = "verres",
    val isLocalProfile: Boolean = false,
    val statsSummary: String? = null
)

data class DrinkPreset(
    val id: String,
    val name: String,
    val volumeMl: Int,
    val alcoholPercent: Double,
    val emoji: String
)

data class BacDrinkSelection(
    val preset: DrinkPreset,
    val quantity: Int = 0
)

enum class BacLevel {
    ZERO,
    LOW,
    MEDIUM,
    HIGH
}

data class BacEstimate(
    val thresholdGPerL: Double,
    val minutesUntilThreshold: Long,
    val estimatedAt: Long
)

data class BacResult(
    val rateGPerL: Double,
    val totalPureAlcoholMl: Double,
    val level: BacLevel,
    val message: String,
    val to050: BacEstimate? = null,
    val to020: BacEstimate? = null
) {
    val isDangerous: Boolean = level == BacLevel.HIGH
}

data class UserStats(
    val detailViews: Int = 0,
    val surpriseOpens: Int = 0,
    val copyActions: Int = 0,
    val shareActions: Int = 0
)

data class LocalPlayerProfile(
    val scoreDeBeauf: Int,
    val detailViews: Int,
    val favorites: Int,
    val customCocktails: Int,
    val surpriseOpens: Int,
    val copyActions: Int,
    val shareActions: Int
) {
    fun toRankingEntry(region: RankingRegion): RankingEntry = RankingEntry(
        id = -region.ordinal - 1,
        name = "Toi, patron du zinc",
        score = scoreDeBeauf,
        badge = "Profil local",
        region = region,
        flag = "🍻",
        scoreLabel = "pts",
        isLocalProfile = true,
        statsSummary = "$detailViews fiches vues • $favorites favoris • $customCocktails créations"
    )
}

fun CustomCocktail.toDetail(): CocktailDetail = CocktailDetail(
    id = id.toString(),
    source = CocktailSource.LOCAL,
    name = name,
    category = "Création du comptoir",
    imageUrl = null,
    ingredients = ingredients,
    instructions = story.ifBlank { "Pas d'histoire, juste une belle inspiration de comptoir." },
    badge = "Maison 🏡",
    accent = "Inventé au PMU"
)

fun CustomCocktail.matchesQuery(query: String): Boolean {
    if (query.isBlank()) return true
    val normalized = query.trim().lowercase()
    return name.lowercase().contains(normalized) ||
        story.lowercase().contains(normalized) ||
        ingredients.any { ingredient ->
            ingredient.name.lowercase().contains(normalized) ||
                ingredient.dose.lowercase().contains(normalized)
        }
}
