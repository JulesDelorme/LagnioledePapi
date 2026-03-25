package com.supdevinci.lagnioledepapi.data

import com.supdevinci.lagnioledepapi.model.BacDrinkSelection
import com.supdevinci.lagnioledepapi.model.BacEstimate
import com.supdevinci.lagnioledepapi.model.BacLevel
import com.supdevinci.lagnioledepapi.model.BacResult
import kotlin.math.ceil

object BacCalculator {
    private const val ELIMINATION_RATE_G_PER_L_PER_HOUR = 0.10

    fun calculate(
        weightKg: Double,
        isMale: Boolean,
        drinks: Collection<BacDrinkSelection>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): BacResult {
        if (weightKg <= 0.0) {
            return BacResult(
                rateGPerL = 0.0,
                totalPureAlcoholMl = 0.0,
                level = BacLevel.ZERO,
                message = "Commence déjà par rentrer ton poids, champion. ⚖️"
            )
        }

        val totalPureAlcoholMl = drinks.sumOf { selection ->
            selection.preset.volumeMl * (selection.preset.alcoholPercent / 100.0) * selection.quantity
        }

        if (totalPureAlcoholMl <= 0.0) {
            return BacResult(
                rateGPerL = 0.0,
                totalPureAlcoholMl = 0.0,
                level = BacLevel.ZERO,
                message = "T'as même pas mouillé la moustache. 🍶"
            )
        }

        val diffusion = if (isMale) 0.7 else 0.6
        val rate = (totalPureAlcoholMl * 0.8) / (diffusion * weightKg)
        val to050 = estimateThreshold(
            currentRate = rate,
            threshold = 0.5,
            currentTimeMillis = currentTimeMillis
        )
        val to020 = estimateThreshold(
            currentRate = rate,
            threshold = 0.2,
            currentTimeMillis = currentTimeMillis
        )
        val level = when {
            rate < 0.5 -> BacLevel.LOW
            rate < 0.8 -> BacLevel.MEDIUM
            else -> BacLevel.HIGH
        }
        val message = when (level) {
            BacLevel.ZERO -> "T'as même pas mouillé la moustache. 🍶"
            BacLevel.LOW -> "T’es encore frais comme un Ricard 🍋"
            BacLevel.MEDIUM -> "Ça commence à chanter faux 🎤"
            BacLevel.HIGH -> "Souffle dans le téléphone voir si t'es encore en vie 🍻"
        }

        return BacResult(
            rateGPerL = rate,
            totalPureAlcoholMl = totalPureAlcoholMl,
            level = level,
            message = message,
            to050 = to050,
            to020 = to020
        )
    }

    private fun estimateThreshold(
        currentRate: Double,
        threshold: Double,
        currentTimeMillis: Long
    ): BacEstimate? {
        if (currentRate <= threshold) return null

        val hoursNeeded = (currentRate - threshold) / ELIMINATION_RATE_G_PER_L_PER_HOUR
        val minutesNeeded = ceil(hoursNeeded * 60.0).toLong().coerceAtLeast(1L)
        return BacEstimate(
            thresholdGPerL = threshold,
            minutesUntilThreshold = minutesNeeded,
            estimatedAt = currentTimeMillis + minutesNeeded * 60_000L
        )
    }
}
