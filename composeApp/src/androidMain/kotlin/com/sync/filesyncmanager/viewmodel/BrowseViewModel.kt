package com.sync.filesyncmanager.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sync.filesyncmanager.RadioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Browse screen
 */
class BrowseViewModel(
    private val repository: RadioRepository
) : ViewModel() {
    private val TAG = "BrowseViewModel"
    // Mutable state flow to update the UI state
    private val _state = MutableStateFlow(BrowseScreenState())
    
    // Immutable state flow to expose to the UI
    val state: StateFlow<BrowseScreenState> = _state.asStateFlow()
    
    // Search debounce job
    private var searchJob: Job? = null
    
    init {
        loadTopStations()
        loadCountries()
    }
    
    /**
     * Load top stations from the API
     */
    fun loadTopStations() {
        _state.update { 
            it.copy(
                stations = ResourceState.Loading,
                selectedCategory = null,
                selectedCountry = null,
                searchQuery = "",
                isSearchActive = false,
                browseMode = BrowseMode.CATEGORIES
            ) 
        }
        
        viewModelScope.launch {
            try {
                val stations = repository.getTopStations()
                _state.update { it.copy(stations = ResourceState.Success(stations)) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(stations = ResourceState.Error("Failed to load stations: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Load stations by category/tag
     */
    fun loadStationsByCategory(category: String) {
        _state.update { 
            it.copy(
                stations = ResourceState.Loading,
                selectedCategory = category,
                selectedCountry = null,
                searchQuery = "",
                isSearchActive = false,
                browseMode = BrowseMode.CATEGORIES
            ) 
        }
        
        viewModelScope.launch {
            try {
                val stations = repository.getStationsByTag(category)
                Log.d(TAG, "Stations loaded: $stations")
                _state.update { it.copy(stations = ResourceState.Success(stations)) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(stations = ResourceState.Error("Failed to load $category stations: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Load list of countries
     */
    private fun loadCountries() {
        _state.update { it.copy(countries = ResourceState.Loading) }
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading countries from API")
                val countries = repository.getCountries()
                Log.d(TAG, "Countries loaded: $countries")
                _state.update { it.copy(countries = ResourceState.Success(countries)) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(countries = ResourceState.Error("Failed to load countries: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Set browse mode
     */
    fun setBrowseMode(mode: BrowseMode) {
        _state.update {
            it.copy(
                browseMode = mode,
                selectedCategory = null,
                selectedCountry = null,
                stations = ResourceState.Loading
            )
        }
        
        when (mode) {
            BrowseMode.CATEGORIES -> loadTopStations()
            BrowseMode.COUNTRIES -> {
                // Just switch to country mode, no stations loaded yet until a country is selected
                _state.update { it.copy(stations = ResourceState.Success(emptyList())) }
            }
            BrowseMode.BY_CLICKS -> loadStationsByClicks()
        }
    }
    
    /**
     * Load stations by country
     */
    fun loadStationsByCountry(country: String) {
        _state.update { 
            it.copy(
                stations = ResourceState.Loading,
                selectedCountry = country,
                selectedCategory = null,
                searchQuery = "",
                isSearchActive = false,
                browseMode = BrowseMode.COUNTRIES
            ) 
        }
        
        viewModelScope.launch {
            try {
                val stations = repository.getStationsByCountry(country)
                _state.update { it.copy(stations = ResourceState.Success(stations)) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(stations = ResourceState.Error("Failed to load stations from $country: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Load stations by clicks (most popular)
     */
    fun loadStationsByClicks() {
        _state.update { 
            it.copy(
                stations = ResourceState.Loading,
                selectedCategory = null,
                selectedCountry = null,
                searchQuery = "",
                isSearchActive = false,
                browseMode = BrowseMode.BY_CLICKS
            ) 
        }
        
        viewModelScope.launch {
            try {
                val stations = repository.getStationsByClicks()
                _state.update { it.copy(stations = ResourceState.Success(stations)) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(stations = ResourceState.Error("Failed to load popular stations: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Search for stations by name with debounce
     */
    fun search(query: String) {
        // Update search query immediately for UI
        _state.update { 
            it.copy(
                searchQuery = query,
                isSearchActive = query.isNotEmpty()
            )
        }
        
        // Cancel any ongoing search job
        searchJob?.cancel()
        
        if (query.isEmpty()) {
            when (_state.value.browseMode) {
                BrowseMode.CATEGORIES -> loadTopStations()
                BrowseMode.COUNTRIES -> {
                    val selectedCountry = _state.value.selectedCountry
                    if (selectedCountry != null) {
                        loadStationsByCountry(selectedCountry)
                    } else {
                        _state.update { it.copy(stations = ResourceState.Success(emptyList())) }
                    }
                }
                BrowseMode.BY_CLICKS -> loadStationsByClicks()
            }
            return
        }
        
        // Debounce search requests to avoid excessive API calls
        searchJob = viewModelScope.launch {
            delay(300) // Wait for 300ms before executing search
            
            _state.update { it.copy(stations = ResourceState.Loading) }
            
            try {
                val searchResults = repository.searchStations(query)
                _state.update { 
                    it.copy(stations = ResourceState.Success(searchResults))
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(stations = ResourceState.Error("Search failed: ${e.message}"))
                }
            }
        }
    }
    
    /**
     * Clear search query
     */
    fun clearSearch() {
        searchJob?.cancel()
        _state.update { it.copy(searchQuery = "", isSearchActive = false) }
        
        when (_state.value.browseMode) {
            BrowseMode.CATEGORIES -> loadTopStations()
            BrowseMode.COUNTRIES -> {
                val selectedCountry = _state.value.selectedCountry
                if (selectedCountry != null) {
                    loadStationsByCountry(selectedCountry)
                } else {
                    _state.update { it.copy(stations = ResourceState.Success(emptyList())) }
                }
            }
            BrowseMode.BY_CLICKS -> loadStationsByClicks()
        }
    }
}