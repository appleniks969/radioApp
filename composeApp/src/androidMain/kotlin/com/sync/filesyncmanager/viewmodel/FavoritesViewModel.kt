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
 * ViewModel for the Favorites screen
 */
class FavoritesViewModel(
    private val repository: RadioRepository
) : ViewModel() {
    
    // Mutable state flow to update the UI state
    private val _state = MutableStateFlow(FavoritesScreenState())
    
    // Immutable state flow to expose to the UI
    val state: StateFlow<FavoritesScreenState> = _state.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    /**
     * Load favorite stations from the repository
     */
    fun loadFavorites() {
        viewModelScope.launch {
            val favorites = repository.getFavoriteStations()
            _state.update { it.copy(favoriteStations = favorites) }
        }
    }
    
    /**
     * Add a station to favorites
     */
    fun addToFavorites(station: RadioStation) {
        repository.addToFavorites(station)
        loadFavorites()
    }
    
    /**
     * Remove a station from favorites
     */
    fun removeFromFavorites(stationId: String) {
        repository.removeFromFavorites(stationId)
        loadFavorites()
    }
    
    /**
     * Check if a station is in favorites
     */
    fun isStationFavorite(stationId: String): Boolean {
        return repository.isStationFavorite(stationId)
    }
}