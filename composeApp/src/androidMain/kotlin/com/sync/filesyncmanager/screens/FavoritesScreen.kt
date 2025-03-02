package com.sync.filesyncmanager.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sync.filesyncmanager.LocalRadioPlayerService
import com.sync.filesyncmanager.LocalRadioRepository
import com.sync.filesyncmanager.LocalViewModelFactory
import com.sync.filesyncmanager.components.RadioStationDetailItem
import com.sync.filesyncmanager.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    onStationClick: (String) -> Unit = {}
) {
    // Get the ViewModel factory from the CompositionLocal
    val factory = LocalViewModelFactory.current
    
    // Create the ViewModel using the factory
    val viewModel: FavoritesViewModel = viewModel(factory = factory!!)
    
    // Get the current state from the ViewModel
    val state by viewModel.state.collectAsState()
    
    val radioPlayerService = LocalRadioPlayerService.current
    val radioRepository = LocalRadioRepository.current
    
    // Refresh favorites list when the screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (state.favoriteStations.isEmpty()) {
            Card(
                modifier = Modifier.padding(vertical = 16.dp),
                elevation = 4.dp
            ) {
                Text(
                    text = "You haven't added any favorite stations yet.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.body1
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.favoriteStations) { station ->
                    RadioStationDetailItem(
                        station = station,
                        onClick = { 
                            radioPlayerService?.playStation(station)
                            radioRepository?.addToRecentStations(station)
                            onStationClick(station.id)
                        },
                        onFavoriteClick = { 
                            viewModel.removeFromFavorites(station.id)
                        },
                        isFavorite = true,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}