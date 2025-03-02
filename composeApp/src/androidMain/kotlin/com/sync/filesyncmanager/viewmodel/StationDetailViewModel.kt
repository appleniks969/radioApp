package com.sync.filesyncmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sync.filesyncmanager.RadioRepository
import com.sync.filesyncmanager.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Station Detail screen
 */
class StationDetailViewModel(
    private val repository: RadioRepository
) : ViewModel() {
    
    // Mutable state flow to update the UI state
    private val _state = MutableStateFlow(StationDetailScreenState())
    
    // Immutable state flow to expose to the UI
    val state: StateFlow<StationDetailScreenState> = _state.asStateFlow()
    
    /**
     * Load a station by ID using an optimized approach
     * This checks local sources before making API calls
     */
    fun loadStation(stationId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                // First check local caches before making API calls
                // Check favorites (fast, in memory)
                var station = repository.getFavoriteStations().find { it.id == stationId }
                
                // Check recent stations (fast, in memory)
                if (station == null) {
                    station = repository.getRecentStations().find { it.id == stationId }
                }
                
                // Check fallback stations (fast, in memory)
                if (station == null) {
                    station = repository.getFallbackStations().find { it.id == stationId }
                }
                
                // If not found in local cache, search API
                if (station == null) {
                    try {
                        // Try direct search if we have a station ID
                        val searchResults = repository.searchStations(stationId, limit = 5)
                        station = searchResults.firstOrNull { it.id == stationId }
                        
                        // If not found with direct search, get top stations as fallback
                        if (station == null) {
                            val topStations = repository.getTopStations(limit = 20)
                            station = topStations.firstOrNull { it.id == stationId }
                        }
                    } catch (e: Exception) {
                        // If API search fails, continue with null station
                        // We'll handle this in the final check
                    }
                }
                
                if (station != null) {
                    val isFavorite = repository.isStationFavorite(stationId)
                    _state.update { 
                        it.copy(
                            station = station, 
                            isLoading = false,
                            isFavorite = isFavorite
                        ) 
                    }
                } else {
                    _state.update { 
                        it.copy(
                            errorMessage = "Station not found", 
                            isLoading = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        errorMessage = "Failed to load station: ${e.message}", 
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Toggle favorite status of a station
     */
    fun toggleFavorite() {
        val currentStation = _state.value.station ?: return
        
        if (_state.value.isFavorite) {
            repository.removeFromFavorites(currentStation.id)
        } else {
            repository.addToFavorites(currentStation)
        }
        
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }
    
    /**
     * Add station to recently played
     */
    fun addToRecentStations(station: RadioStation) {
        repository.addToRecentStations(station)
    }
}