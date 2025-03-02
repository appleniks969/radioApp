package com.sync.filesyncmanager.viewmodel

import com.sync.filesyncmanager.RadioStation

/**
 * Represents the different states of a data fetch operation
 */
sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val message: String) : ResourceState<Nothing>()
}

/**
 * Represents the state of the home screen
 */
data class HomeScreenState(
    val featuredStations: ResourceState<List<RadioStation>> = ResourceState.Loading,
    val recentStations: List<RadioStation> = emptyList(),
    val categories: List<String> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

/**
 * Represents the state of the browse screen
 */
data class BrowseScreenState(
    val stations: ResourceState<List<RadioStation>> = ResourceState.Loading,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedCountry: String? = null,
    val isSearchActive: Boolean = false,
    val categories: List<String> = listOf("Classical", "Pop", "Rock", "Jazz", "News", "Talk", "Sport"),
    val countries: ResourceState<List<String>> = ResourceState.Loading,
    val browseMode: BrowseMode = BrowseMode.CATEGORIES
)

/**
 * Represents different browse modes
 */
enum class BrowseMode {
    CATEGORIES,
    COUNTRIES,
    BY_CLICKS
}

/**
 * Represents the state of the favorites screen
 */
data class FavoritesScreenState(
    val favoriteStations: List<RadioStation> = emptyList()
)

/**
 * Represents the state of the station detail screen
 */
data class StationDetailScreenState(
    val station: RadioStation? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false
)

/**
 * Represents the state of the now playing screen
 */
data class NowPlayingScreenState(
    val currentStation: RadioStation? = null,
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)