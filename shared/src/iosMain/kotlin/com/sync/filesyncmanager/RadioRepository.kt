package com.sync.filesyncmanager

/**
 * iOS implementation of the RadioRepository
 * This is a stub implementation since we're focusing on Android for now
 */
class IosRadioRepository : RadioRepository {
    
    // In-memory cache of stations
    private val favoriteStations = mutableListOf<RadioStation>()
    private val recentStations = mutableListOf<RadioStation>()
    
    override suspend fun getTopStations(limit: Int, offset: Int): List<RadioStation> {
        return getFallbackStations()
    }
    
    override suspend fun searchStations(query: String, limit: Int, offset: Int): List<RadioStation> {
        return getFallbackStations().filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.genre.contains(query, ignoreCase = true) 
        }
    }
    
    override suspend fun getStationsByTag(tag: String, limit: Int, offset: Int): List<RadioStation> {
        return getFallbackStations().filter { 
            it.genre.equals(tag, ignoreCase = true) 
        }
    }
    
    override suspend fun getStationsByCountry(country: String, limit: Int, offset: Int): List<RadioStation> {
        return getFallbackStations()
    }
    
    override suspend fun getRecentlyAddedStations(limit: Int, offset: Int): List<RadioStation> {
        return getFallbackStations()
    }
    
    override fun getFavoriteStations(): List<RadioStation> {
        return favoriteStations
    }
    
    override fun addToFavorites(station: RadioStation) {
        if (!isStationFavorite(station.id)) {
            favoriteStations.add(station)
        }
    }
    
    override fun removeFromFavorites(stationId: String) {
        val station = favoriteStations.find { it.id == stationId }
        if (station != null) {
            favoriteStations.remove(station)
        }
    }
    
    override fun isStationFavorite(stationId: String): Boolean {
        return favoriteStations.any { it.id == stationId }
    }
    
    override fun getRecentStations(): List<RadioStation> {
        return recentStations
    }
    
    override fun addToRecentStations(station: RadioStation) {
        // Remove station if already exists
        val iterator = recentStations.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id == station.id) {
                iterator.remove()
            }
        }
        
        // Add to the beginning of the list
        recentStations.add(0, station)
        
        // Keep only the most recent stations
        if (recentStations.size > 5) {
            recentStations.removeAt(recentStations.size - 1)
        }
    }
    
    override fun getFallbackStations(): List<RadioStation> = FALLBACK_STATIONS
}