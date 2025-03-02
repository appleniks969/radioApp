package com.sync.filesyncmanager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sync.filesyncmanager.LocalRadioPlayerService
import com.sync.filesyncmanager.LocalRadioRepository
import com.sync.filesyncmanager.LocalViewModelFactory
import com.sync.filesyncmanager.RadioStation
import com.sync.filesyncmanager.utils.ColorUtils.getRandomColorForStation
import com.sync.filesyncmanager.viewmodel.StationDetailViewModel

@Composable
fun StationDetailScreen(
    stationId: String,
    onBackClick: () -> Unit,
    onPlayClick: (RadioStation) -> Unit
) {
    // Get the ViewModel factory from the CompositionLocal
    val factory = LocalViewModelFactory.current
    
    // Create the ViewModel using the factory
    val viewModel: StationDetailViewModel = viewModel(factory = factory!!)
    
    // Get the current state from the ViewModel
    val state by viewModel.state.collectAsState()
    
    val radioPlayerService = LocalRadioPlayerService.current
    val radioRepository = LocalRadioRepository.current
    
    // Load the station when the screen is shown
    LaunchedEffect(stationId) {
        viewModel.loadStation(stationId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Text(
                text = "Station Detail",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Empty spacer to balance the back button
            Box(modifier = Modifier.size(48.dp))
        }
        
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        } else if (state.station != null) {
            val station = state.station!!
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Station Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(getRandomColorForStation(station))
            ) {
                Text(
                    text = station.name.take(3).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Station Info
            Text(
                text = station.name,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = station.genre,
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = station.description,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Play Button
            Button(
                onClick = { 
                    onPlayClick(station)
                    viewModel.addToRecentStations(station)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                content = {
                    Text(
                        text = "PLAY",
                        style = MaterialTheme.typography.button,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Favorite Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (state.isFavorite) Color.Red else MaterialTheme.colors.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = if (state.isFavorite) "Favorited" else "Favorite",
                        style = MaterialTheme.typography.caption
                    )
                }
                
                // Share Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { /* Open share dialog */ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Share",
                        style = MaterialTheme.typography.caption
                    )
                }
                
                // More Options Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { /* Show more options */ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // We could load similar stations here based on the genre
            // For now, we'll just show a message
            Text(
                text = "Similar stations will appear here",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SimilarStationItem(
    station: RadioStation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}