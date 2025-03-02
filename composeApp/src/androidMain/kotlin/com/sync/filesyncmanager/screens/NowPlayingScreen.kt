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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sync.filesyncmanager.LocalRadioPlayerService
import com.sync.filesyncmanager.LocalViewModelFactory
import com.sync.filesyncmanager.utils.ColorUtils.getRandomColorForStation
import com.sync.filesyncmanager.viewmodel.NowPlayingViewModel

@Composable
fun NowPlayingScreen(
    stationId: String,
    onBackClick: () -> Unit
) {
    // Get the ViewModel factory from the CompositionLocal
    val factory = LocalViewModelFactory.current
    
    // Create the ViewModel using the factory
    val viewModel: NowPlayingViewModel = viewModel(factory = factory!!)
    
    // Get the current state from the ViewModel
    val state by viewModel.state.collectAsState()
    
    val radioPlayerService = LocalRadioPlayerService.current
    
    // Remember volume locally (just for UI, not actually implemented)
    var volume by remember { mutableStateOf(0.8f) }
    
    // Initialize the ViewModel with the station ID
    LaunchedEffect(stationId) {
        viewModel.init(stationId)
    }
    
    // If we have an error, display it
    if (state.errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
        return
    }
    
    // If we don't have a station yet, show loading
    if (state.currentStation == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    val station = state.currentStation!!
    val primaryColor = getRandomColorForStation(station)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor.copy(alpha = 0.9f))
    ) {
        // Top Bar with Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        
        // Main Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(top = 40.dp)
        ) {
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.h6,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Station Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            ) {
                Text(
                    text = station.name.take(3).uppercase(),
                    color = primaryColor,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Station Information
            Text(
                text = station.name,
                style = MaterialTheme.typography.h5,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Currently Playing:",
                style = MaterialTheme.typography.body1,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "\"${station.genre} Live Stream\"",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Playback Controls
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Previous Button (disabled for now)
                IconButton(
                    onClick = { /* Previous station - Not implemented */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White.copy(alpha = 0.5f) // Dimmed to indicate disabled
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Play/Pause Button
                FloatingActionButton(
                    onClick = {
                        viewModel.togglePlayback()
                    },
                    modifier = Modifier.size(72.dp),
                    backgroundColor = Color.White
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(40.dp),
                        tint = primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Next Button (disabled for now)
                IconButton(
                    onClick = { /* Next station - Not implemented */ },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White.copy(alpha = 0.5f) // Dimmed to indicate disabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Volume Slider
            Slider(
                value = volume,
                onValueChange = { volume = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Additional Controls
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
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (state.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (state.isFavorite) Color.Red else Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Favorite",
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                }
                
                // Sleep Timer (not implemented)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { /* Set sleep timer - Not implemented */ },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Sleep Timer",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Sleep Timer",
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                }
            }
        }
    }
}