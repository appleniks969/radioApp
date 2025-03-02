package com.sync.filesyncmanager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sync.filesyncmanager.network.RadioBrowserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of the RadioRepository
 */
class AndroidRadioRepository(
    private val api: RadioBrowserApi,
    private val context: Context
) : RadioRepository {
    
    private val gson = Gson()
    
    // In-memory cache of stations
    private val favoriteStations = mutableListOf<RadioStation>()
    private val recentStations = mutableListOf<RadioStation>()
    
    // SharedPreferences keys
    private companion object {
        const val PREFS_NAME = "radio_prefs"
        const val FAVORITES_KEY = "favorite_stations"
        const val RECENT_STATIONS_KEY = "recent_stations"
        const val TAG = "RadioRepository"
        const val MAX_RECENT_STATIONS = 5
    }
    
    init {
        loadFavorites()
        loadRecentStations()
    }
    
    // Get SharedPreferences
    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Load favorites from SharedPreferences
     */
    private fun loadFavorites() {
        try {
            val json = getPrefs().getString(FAVORITES_KEY, null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<List<RadioStation>>() {}.type
                val loadedStations: List<RadioStation> = gson.fromJson(json, type)
                favoriteStations.clear()
                favoriteStations.addAll(loadedStations)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading favorites: ${e.message}")
        }
    }
    
    /**
     * Save favorites to SharedPreferences
     */
    private fun saveFavorites() {
        try {
            val json = gson.toJson(favoriteStations)
            getPrefs().edit().putString(FAVORITES_KEY, json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving favorites: ${e.message}")
        }
    }
    
    /**
     * Load recent stations from SharedPreferences
     */
    private fun loadRecentStations() {
        try {
            val json = getPrefs().getString(RECENT_STATIONS_KEY, null)
            if (!json.isNullOrEmpty()) {
                val type = object : TypeToken<List<RadioStation>>() {}.type
                val loadedStations: List<RadioStation> = gson.fromJson(json, type)
                recentStations.clear()
                recentStations.addAll(loadedStations)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading recent stations: ${e.message}")
        }
    }
    
    /**
     * Save recent stations to SharedPreferences
     */
    private fun saveRecentStations() {
        try {
            val json = gson.toJson(recentStations)
            getPrefs().edit().putString(RECENT_STATIONS_KEY, json).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving recent stations: ${e.message}")
        }
    }
    
    /**
     * Get top stations by votes
     */
    override suspend fun getTopStations(limit: Int, offset: Int): List<RadioStation> = 
        withContext(Dispatchers.IO) {
            try {
                api.getTopStations(limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting top stations: ${e.message}")
                getFallbackStations()
            }
        }
    
    /**
     * Search stations by name
     */
    override suspend fun searchStations(query: String, limit: Int, offset: Int): List<RadioStation> = 
        withContext(Dispatchers.IO) {
            try {
                api.searchStations(query, limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error searching stations: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get stations by tag (genre)
     */
    override suspend fun getStationsByTag(tag: String, limit: Int, offset: Int): List<RadioStation> = 
        withContext(Dispatchers.IO) {
            try {
                api.getStationsByTag(tag, limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting stations by tag: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get stations by country
     */
    override suspend fun getStationsByCountry(country: String, limit: Int, offset: Int): List<RadioStation> = 
        withContext(Dispatchers.IO) {
            try {
                api.getStationsByCountry(country, limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting stations by country: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get recently added stations
     */
    override suspend fun getRecentlyAddedStations(limit: Int, offset: Int): List<RadioStation> = 
        withContext(Dispatchers.IO) {
            try {
                api.getRecentlyAddedStations(limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting recently added stations: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get all countries
     */
    override suspend fun getCountries(): List<String> =
        withContext(Dispatchers.IO) {
            try {
                api.getCountries(true)
                    .map { it["name"] ?: "" }
                    .filter { it.isNotEmpty() }
                    .sorted()
            } catch (e: Exception) {
                Log.e(TAG, "Error getting countries: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get stations sorted by clicks (popularity)
     */
    override suspend fun getStationsByClicks(limit: Int, offset: Int): List<RadioStation> =
        withContext(Dispatchers.IO) {
            try {
                api.getStationsByClicks(limit, offset, true)
                    .map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting stations by clicks: ${e.message}")
                emptyList()
            }
        }
    
    /**
     * Get favorite stations
     */
    override fun getFavoriteStations(): List<RadioStation> {
        return favoriteStations.toList()
    }
    
    /**
     * Add a station to favorites
     */
    override fun addToFavorites(station: RadioStation) {
        if (!isStationFavorite(station.id)) {
            favoriteStations.add(station)
            saveFavorites()
        }
    }
    
    /**
     * Remove a station from favorites
     */
    override fun removeFromFavorites(stationId: String) {
        val station = favoriteStations.find { it.id == stationId }
        if (station != null) {
            favoriteStations.remove(station)
            saveFavorites()
        }
    }
    
    /**
     * Check if a station is in favorites
     */
    override fun isStationFavorite(stationId: String): Boolean {
        return favoriteStations.any { it.id == stationId }
    }
    
    /**
     * Get recently played stations
     */
    override fun getRecentStations(): List<RadioStation> {
        return recentStations.toList()
    }
    
    /**
     * Add a station to recently played
     */
    override fun addToRecentStations(station: RadioStation) {
        // Remove if already exists to avoid duplicates
        recentStations.removeIf { it.id == station.id }
        
        // Add to the beginning of the list
        recentStations.add(0, station)
        
        // Keep only the most recent stations
        if (recentStations.size > MAX_RECENT_STATIONS) {
            recentStations.removeAt(recentStations.size - 1)
        }
        
        saveRecentStations()
    }
    
    /**
     * Get fallback stations in case API fails
     */
    override fun getFallbackStations(): List<RadioStation> = FALLBACK_STATIONS
}