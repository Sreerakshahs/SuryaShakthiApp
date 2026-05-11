package com.suryashaktiapp.utils



import kotlin.random.Random

object EnergyCalculator {

    fun calculateSavings(generated: Float, consumed: Float, ratePerUnit: Float): Float {
        return if (generated >= consumed) {
            consumed * ratePerUnit
        } else {
            generated * ratePerUnit
        }
    }

    fun calculateExport(generated: Float, consumed: Float): Float {
        return if (generated > consumed) generated - consumed else 0f
    }

    fun calculateIndependenceScore(generated: Float, consumed: Float, battery: Float): Int {
        if (consumed == 0f) return 100
        val solarCoverage = (generated / consumed).coerceAtMost(1f) * 70f
        val batteryBonus = (battery / 100f) * 30f
        return (solarCoverage + batteryBonus).toInt().coerceIn(0, 100)
    }

    fun simulateGeneration(weather: String, panelCapacityKw: Float = 2.0f): Float {
        val peakSunHours = when (weather) {
            "Sunny"         -> Random.nextFloat() * 2 + 5f
            "Partly Cloudy" -> Random.nextFloat() * 2 + 3f
            "Cloudy"        -> Random.nextFloat() * 1.5f + 1f
            "Rainy"         -> Random.nextFloat() * 0.5f
            else            -> 4f
        }
        return panelCapacityKw * peakSunHours
    }

    fun getPeakSuggestion(weather: String, currentHour: Int): String {
        return when {
            weather == "Sunny" && currentHour in 10..15 ->
                "☀️ Peak Sun Hours! Ideal time to run washing machine, water pump & iron."
            weather == "Sunny" && currentHour in 8..17 ->
                "🌤️ Good solar generation. Consider charging devices now."
            weather == "Partly Cloudy" ->
                "⛅ Moderate generation. Avoid heavy appliances simultaneously."
            weather == "Cloudy" || weather == "Rainy" ->
                "🌧️ Low solar output. Conserve battery for essentials."
            currentHour !in 6..18 ->
                "🌙 Night mode. Running on battery/grid. Minimize usage."
            else -> "✅ Moderate solar available. Monitor your consumption."
        }
    }
}