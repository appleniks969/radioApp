package com.sync.filesyncmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.sync.filesyncmanager.repository.AppContainer
import com.sync.filesyncmanager.service.RadioPlayerService
import com.sync.filesyncmanager.viewmodel.ViewModelFactory

// Create CompositionLocal providers for dependencies
val LocalRadioPlayerService = compositionLocalOf<RadioPlayerService?> { null }
val LocalViewModelFactory = compositionLocalOf<ViewModelProvider.Factory?> { null }
val LocalRadioRepository = compositionLocalOf<RadioRepository?> { null }

class MainActivity : ComponentActivity() {
    private lateinit var radioPlayerService: RadioPlayerService
    private lateinit var viewModelFactory: ViewModelFactory
    
    private val appContainer: AppContainer by lazy {
        (application as MainApplication).container
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the repository from the container
        val radioRepository = appContainer.radioRepository
        
        // Initialize the RadioPlayerService
        radioPlayerService = RadioPlayerService(this)
        
        // Create the ViewModelFactory
        viewModelFactory = ViewModelFactory(radioRepository, radioPlayerService)

        setContent {
            // Provide dependencies to the composition
            CompositionLocalProvider(
                LocalRadioPlayerService provides radioPlayerService,
                LocalRadioRepository provides radioRepository,
                LocalViewModelFactory provides viewModelFactory
            ) {
                App()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Release the player when the activity is destroyed
        radioPlayerService.release()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // In the preview, we provide mock dependencies
    val mockRepository = FALLBACK_STATIONS.let {
        object : RadioRepository {
            override suspend fun getTopStations(limit: Int, offset: Int) = it
            override suspend fun searchStations(query: String, limit: Int, offset: Int) = 
                it.filter { station -> station.name.contains(query, ignoreCase = true) }
            override suspend fun getStationsByTag(tag: String, limit: Int, offset: Int) = 
                it.filter { station -> station.genre.equals(tag, ignoreCase = true) }
            override suspend fun getStationsByCountry(country: String, limit: Int, offset: Int) = emptyList<RadioStation>()
            override suspend fun getRecentlyAddedStations(limit: Int, offset: Int) = emptyList<RadioStation>()
            override suspend fun getCountries(): List<String> = listOf("United Kingdom", "United States", "Germany", "France")
            override suspend fun getStationsByClicks(limit: Int, offset: Int): List<RadioStation> = it
            override fun getFavoriteStations() = emptyList<RadioStation>()
            override fun addToFavorites(station: RadioStation) {}
            override fun removeFromFavorites(stationId: String) {}
            override fun isStationFavorite(stationId: String) = false
            override fun getRecentStations() = emptyList<RadioStation>()
            override fun addToRecentStations(station: RadioStation) {}
            override fun getFallbackStations() = it
        }
    }
    
    CompositionLocalProvider(
        LocalRadioPlayerService provides null,
        LocalRadioRepository provides mockRepository,
        LocalViewModelFactory provides ViewModelFactory(mockRepository)
    ) {
        App()
    }
}