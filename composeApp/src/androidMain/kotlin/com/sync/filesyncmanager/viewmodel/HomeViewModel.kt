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
 * ViewModel for the Home screen
 */
class HomeViewModel(
    private val repository: RadioRepository
) : ViewModel() {
    
    // Mutable state flow to update the UI state
    private val _state = MutableStateFlow(HomeScreenState())
    
    // Immutable state flow to expose to the UI
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    
    // Constants
    private companion object {
        const val FEATURED_STATIONS_LIMIT = 10
        val DEFAULT_CATEGORIES = listOf("Classical", "Pop", "Rock", "Jazz", "News", "Talk", "Sport")
    }
    
    init {
        loadInitialData()
    }
    
    /**
     * Load initial data for the home screen
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            // Set categories
            _state.update { it.copy(categories = DEFAULT_CATEGORIES) }
            
            // Get recent stations from repository
            val recentStations = repository.getRecentStations()
            _state.update { it.copy(recentStations = recentStations) }
            
            // Load featured stations from the API
            loadFeaturedStations()
        }
    }
    
    /**
     * Load featured stations from the API
     */
    private fun loadFeaturedStations() {
        _state.update { it.copy(featuredStations = ResourceState.Loading) }
        
        viewModelScope.launch {
            try {
                // Get top stations from repository
                val stations = repository.getTopStations(limit = FEATURED_STATIONS_LIMIT)
                if (stations.isNotEmpty()) {
                    _state.update { it.copy(featuredStations = ResourceState.Success(stations)) }
                } else {
                    _state.update { 
                        it.copy(featuredStations = ResourceState.Success(repository.getFallbackStations())) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        featuredStations = ResourceState.Error("Failed to load featured stations: ${e.message}")
                    ) 
                }
            }
        }
    }
    
    /**
     * Search for stations by name
     */
    fun search(query: String) {
        _state.update { it.copy(searchQuery = query, isSearchActive = query.isNotEmpty()) }
        
        if (query.isEmpty()) {
            loadFeaturedStations()
            return
        }
        
        _state.update { it.copy(featuredStations = ResourceState.Loading) }
        
        viewModelScope.launch {
            try {
                val searchResults = repository.searchStations(query)
                _state.update { it.copy(featuredStations = ResourceState.Success(searchResults)) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        featuredStations = ResourceState.Error("Search failed: ${e.message}")
                    ) 
                }
            }
        }
    }
    
    /**
     * Clear search and reset to default state
     */
    fun clearSearch() {
        _state.update { it.copy(searchQuery = "", isSearchActive = false) }
        loadFeaturedStations()
    }
    
    /**
     * Add station to recently played
     */
    fun addToRecentStations(station: RadioStation) {
        repository.addToRecentStations(station)
        
        // Update the state with the new recent stations
        val updatedRecentStations = repository.getRecentStations()
        _state.update { it.copy(recentStations = updatedRecentStations) }
    }
}