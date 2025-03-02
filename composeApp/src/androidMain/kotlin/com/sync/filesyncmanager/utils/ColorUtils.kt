package com.sync.filesyncmanager.utils

import androidx.compose.ui.graphics.Color
import com.sync.filesyncmanager.RadioStation

/**
 * Utility functions for color handling in the app
 */
object ColorUtils {
    
    /**
     * Returns a consistent color for a given radio station based on its ID
     */
    fun getRandomColorForStation(station: RadioStation): Color {
        val colors = listOf(
            Color(0xFF6C63FF), // Primary purple
            Color(0xFFFF6584), // Pink
            Color(0xFFFFC75F), // Yellow/Orange
            Color(0xFF845EC2)  // Deep purple
        )
        return colors[station.id.hashCode().rem(colors.size).absoluteValue]
    }
    
    private val Int.absoluteValue: Int
        get() = if (this < 0) -this else this
        
    /**
     * Returns a country flag color for a given station
     */
    fun getCountryColorForStation(station: RadioStation): Color {
        return when {
            getCountryForStation(station) == "United Kingdom" -> Color(0xFF012169) // UK Blue
            getCountryForStation(station) == "United States" -> Color(0xFFB22234) // US Red
            else -> Color(0xFF2E7D32) // Default Green
        }
    }
    
    /**
     * Returns the country for a station based on its name
     */
    fun getCountryForStation(station: RadioStation): String {
        return when {
            station.name.contains("BBC", ignoreCase = true) -> "United Kingdom"
            station.name.contains("KEXP", ignoreCase = true) -> "United States"
            station.name.contains("Jazz FM", ignoreCase = true) -> "United Kingdom"
            station.name.contains("Capital", ignoreCase = true) -> "United Kingdom"
            else -> "International"
        }
    }
}