package com.sync.filesyncmanager

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

/**
 * Domain model for a radio station
 */
data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val imageUrl: String,
    val genre: String,
    val description: String
)

/**
 * Repository interface to manage radio stations data
 * Provides methods to get stations from the Radio Browser API
 * Handles caching and favorite stations management
 */
interface RadioRepository {
    // API methods
    suspend fun getTopStations(limit: Int = 100, offset: Int = 0): List<RadioStation>
    suspend fun searchStations(query: String, limit: Int = 100, offset: Int = 0): List<RadioStation>
    suspend fun getStationsByTag(tag: String, limit: Int = 100, offset: Int = 0): List<RadioStation>
    suspend fun getStationsByCountry(country: String, limit: Int = 100, offset: Int = 0): List<RadioStation>
    suspend fun getRecentlyAddedStations(limit: Int = 100, offset: Int = 0): List<RadioStation>
    suspend fun getCountries(): List<String>
    suspend fun getStationsByClicks(limit: Int = 100, offset: Int = 0): List<RadioStation>
    
    // Favorites management
    fun getFavoriteStations(): List<RadioStation>
    fun addToFavorites(station: RadioStation)
    fun removeFromFavorites(stationId: String)
    fun isStationFavorite(stationId: String): Boolean
    
    // Recent stations
    fun getRecentStations(): List<RadioStation>
    fun addToRecentStations(station: RadioStation)
    
    // Default fallback stations
    fun getFallbackStations(): List<RadioStation>
}

/**
 * Common fallback stations for all platforms
 */
val FALLBACK_STATIONS = listOf(
    RadioStation(
        id = "1",
        name = "Classic FM",
        streamUrl = "https://media-ice.musicradio.com/ClassicFMMP3",
        imageUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/c/c7/Classic_FM_logo.svg/1200px-Classic_FM_logo.svg.png",
        genre = "Classical",
        description = "The UK's only 100% classical music radio station"
    ),
    RadioStation(
        id = "2",
        name = "BBC Radio 1",
        streamUrl = "https://stream.live.vc.bbcmedia.co.uk/bbc_radio_one",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/BBC_Radio_1_2021.svg/1200px-BBC_Radio_1_2021.svg.png",
        genre = "Contemporary",
        description = "The best new music and entertainment"
    ),
    RadioStation(
        id = "3",
        name = "Jazz FM",
        streamUrl = "https://stream-al.planetradio.co.uk/jazzhigh.aac",
        imageUrl = "https://upload.wikimedia.org/wikipedia/en/9/9a/Jazz_FM_logo_2018.png",
        genre = "Jazz",
        description = "The UK's leading jazz, soul and blues broadcaster"
    ),
    RadioStation(
        id = "4",
        name = "Capital FM",
        streamUrl = "https://media-ice.musicradio.com/CapitalUKMP3",
        imageUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/b/b8/Capital_FM_2022.svg/1200px-Capital_FM_2022.svg.png",
        genre = "Pop",
        description = "The UK's No.1 Hit Music Station"
    )
)