package com.supdevinci.lagnioledepapi.data

import com.supdevinci.lagnioledepapi.model.BacDrinkSelection
import com.supdevinci.lagnioledepapi.model.BacLevel
import com.supdevinci.lagnioledepapi.model.DrinkPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BacCalculatorTest {
    private val beer = DrinkPreset("beer", "Bière", 330, 5.0, "🍺")
    private val wine = DrinkPreset("wine", "Vin", 150, 12.0, "🍷")
    private val baseTime = 1_700_000_000_000L

    @Test
    fun calculate_maleSingleBeer_returnsExpectedRate() {
        val result = BacCalculator.calculate(
            weightKg = 70.0,
            isMale = true,
            drinks = listOf(BacDrinkSelection(beer, quantity = 1)),
            currentTimeMillis = baseTime
        )

        assertEquals(0.27, result.rateGPerL, 0.01)
        assertEquals(BacLevel.LOW, result.level)
    }

    @Test
    fun calculate_femaleSingleWine_returnsExpectedRate() {
        val result = BacCalculator.calculate(
            weightKg = 60.0,
            isMale = false,
            drinks = listOf(BacDrinkSelection(wine, quantity = 1)),
            currentTimeMillis = baseTime
        )

        assertEquals(0.40, result.rateGPerL, 0.01)
        assertEquals(BacLevel.LOW, result.level)
    }

    @Test
    fun calculate_multipleDrinks_accumulatesAlcohol() {
        val result = BacCalculator.calculate(
            weightKg = 80.0,
            isMale = true,
            drinks = listOf(
                BacDrinkSelection(beer, quantity = 2),
                BacDrinkSelection(wine, quantity = 1)
            ),
            currentTimeMillis = baseTime
        )

        assertEquals(51.0, result.totalPureAlcoholMl, 0.01)
        assertEquals(0.73, result.rateGPerL, 0.01)
        assertEquals(BacLevel.MEDIUM, result.level)
    }

    @Test
    fun calculate_invalidWeight_returnsZero() {
        val result = BacCalculator.calculate(
            weightKg = 0.0,
            isMale = true,
            drinks = listOf(BacDrinkSelection(beer, quantity = 1)),
            currentTimeMillis = baseTime
        )

        assertEquals(0.0, result.rateGPerL, 0.0)
        assertEquals(BacLevel.ZERO, result.level)
    }

    @Test
    fun calculate_zeroQuantities_returnsZeroMessage() {
        val result = BacCalculator.calculate(
            weightKg = 75.0,
            isMale = true,
            drinks = listOf(BacDrinkSelection(beer, quantity = 0)),
            currentTimeMillis = baseTime
        )

        assertEquals(0.0, result.rateGPerL, 0.0)
        assertEquals(BacLevel.ZERO, result.level)
        assertTrue(result.message.contains("moustache"))
        assertNull(result.to050)
        assertNull(result.to020)
    }

    @Test
    fun calculate_aboveThresholds_returnsTwoEstimates() {
        val result = BacCalculator.calculate(
            weightKg = 70.0,
            isMale = true,
            drinks = listOf(
                BacDrinkSelection(beer, quantity = 4),
                BacDrinkSelection(wine, quantity = 2)
            ),
            currentTimeMillis = baseTime
        )

        assertTrue(result.rateGPerL > 0.8)
        assertNotNull(result.to050)
        assertNotNull(result.to020)
        assertTrue(result.to050!!.minutesUntilThreshold > 0)
        assertTrue(result.to020!!.estimatedAt > result.to050!!.estimatedAt)
    }

    @Test
    fun calculate_belowThreshold_skipsEstimateForAlreadyPassedTarget() {
        val result = BacCalculator.calculate(
            weightKg = 70.0,
            isMale = true,
            drinks = listOf(BacDrinkSelection(beer, quantity = 1)),
            currentTimeMillis = baseTime
        )

        assertNull(result.to050)
        assertTrue(result.to020 != null)
    }
}
