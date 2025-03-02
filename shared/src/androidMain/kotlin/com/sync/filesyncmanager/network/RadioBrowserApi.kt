package com.sync.filesyncmanager.network

import com.sync.filesyncmanager.network.model.RadioStationDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Retrofit API interface for the Radio Browser API
 * Documentation: https://de1.api.radio-browser.info/
 */
interface RadioBrowserApi {
    
    /**
     * Get stations by popularity (votes/clicks)
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/topvote")
    suspend fun getTopStations(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
    
    /**
     * Search stations by name
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/byname")
    suspend fun searchStations(
        @Query("name") query: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
    
    /**
     * Get stations by tag (genre)
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/bytag")
    suspend fun getStationsByTag(
        @Query("tag") tag: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
    
    /**
     * Get stations by country
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/bycountry")
    suspend fun getStationsByCountry(
        @Query("country") country: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
    
    /**
     * Get recently added stations
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/lastchange")
    suspend fun getRecentlyAddedStations(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
    
    /**
     * Get list of all countries
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/countries")
    suspend fun getCountries(
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<Map<String, String>>
    
    /**
     * Get stations sorted by clicks
     */
    @Headers("User-Agent: RadioExplorerApp/1.0")
    @GET("json/stations/byclicks")
    suspend fun getStationsByClicks(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("hidebroken") hideBroken: Boolean = true
    ): List<RadioStationDto>
}