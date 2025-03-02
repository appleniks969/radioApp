package com.sync.filesyncmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sync.filesyncmanager.RadioRepository
import com.sync.filesyncmanager.RadioStation
import com.sync.filesyncmanager.service.RadioPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Now Playing screen
 */
class NowPlayingViewModel(
    private val repository: RadioRepository,
    private val radioPlayerService: RadioPlayerService? = null
) : ViewModel() {
    
    // Mutable state flow to update the UI state
    private val _state = MutableStateFlow(NowPlayingScreenState())
    
    // Immutable state flow to expose to the UI
    val state: StateFlow<NowPlayingScreenState> = _state.asStateFlow()
    
    /**
     * Initialize the view model with a station ID
     */
    fun init(stationId: String) {
        _state.update { it.copy(errorMessage = null) }
        
        viewModelScope.launch {
            try {
                // Check if a station is already playing
                val currentlyPlaying = radioPlayerService?.getCurrentStation()
                
                if (currentlyPlaying != null && currentlyPlaying.id == stationId) {
                    // We're already playing this station
                    updateWithStation(currentlyPlaying)
                } else {
                    // We need to find and load this station
                    loadStation(stationId)
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(errorMessage = "Failed to initialize player: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Load a station by ID
     */
    private suspend fun loadStation(stationId: String) {
        try {
            // First check top stations
            val allStations = repository.getTopStations(limit = 100)
            var station = allStations.find { it.id == stationId }
            
            // If not found, check favorites
            if (station == null) {
                station = repository.getFavoriteStations().find { it.id == stationId }
            }
            
            // If not found, check recent stations
            if (station == null) {
                station = repository.getRecentStations().find { it.id == stationId }
            }
            
            // If still not found, check fallback stations
            if (station == null) {
                station = repository.getFallbackStations().find { it.id == stationId }
            }
            
            if (station != null) {
                updateWithStation(station)
                
                // Start playing if not already playing
                if (radioPlayerService?.getCurrentStation()?.id != station.id) {
                    radioPlayerService?.playStation(station)
                    updatePlayingState()
                }
                
                // Add to recent stations
                repository.addToRecentStations(station)
            } else {
                _state.update { 
                    it.copy(errorMessage = "Station not found")
                }
            }
        } catch (e: Exception) {
            _state.update { 
                it.copy(errorMessage = "Failed to load station: ${e.message}")
            }
        }
    }
    
    /**
     * Update the state with a station
     */
    private fun updateWithStation(station: RadioStation) {
        val isFavorite = repository.isStationFavorite(station.id)
        val isPlaying = radioPlayerService?.isPlaying() ?: false
        
        _state.update { 
            it.copy(
                currentStation = station,
                isFavorite = isFavorite,
                isPlaying = isPlaying
            )
        }
    }
    
    /**
     * Toggle play/pause
     */
    fun togglePlayback() {
        radioPlayerService?.togglePlayback()
        updatePlayingState()
    }
    
    /**
     * Update the playing state
     */
    private fun updatePlayingState() {
        val isPlaying = radioPlayerService?.isPlaying() ?: false
        _state.update { it.copy(isPlaying = isPlaying) }
    }
    
    /**
     * Toggle favorite status of the current station
     */
    fun toggleFavorite() {
        val currentStation = _state.value.currentStation ?: return
        
        if (_state.value.isFavorite) {
            repository.removeFromFavorites(currentStation.id)
        } else {
            repository.addToFavorites(currentStation)
        }
        
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }
}