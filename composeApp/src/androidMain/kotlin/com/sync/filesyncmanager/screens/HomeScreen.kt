package com.sync.filesyncmanager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sync.filesyncmanager.LocalRadioPlayerService
import com.sync.filesyncmanager.LocalViewModelFactory
import com.sync.filesyncmanager.RadioStation
import com.sync.filesyncmanager.components.FeaturedStationItem
import com.sync.filesyncmanager.components.RadioStationItem
import com.sync.filesyncmanager.viewmodel.HomeViewModel
import com.sync.filesyncmanager.viewmodel.ResourceState

@Composable
fun HomeScreen(
    onStationClick: (String) -> Unit = {}
) {
    // Get the ViewModel factory from the CompositionLocal
    val factory = LocalViewModelFactory.current
    
    // Create the ViewModel using the factory
    val viewModel: HomeViewModel = viewModel(factory = factory!!)
    
    // Get the current state from the ViewModel
    val state by viewModel.state.collectAsState()
    
    // Get the RadioPlayerService from the CompositionLocal
    val radioPlayerService = LocalRadioPlayerService.current
    
    // Track the search query
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Radio Explorer",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search stations...") },
            singleLine = true,
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { 
                            searchQuery = ""
                            viewModel.clearSearch()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                backgroundColor = Color.White
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Featured Stations Section
        Text(
            text = if (state.isSearchActive) "Search Results" else "Featured Stations",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Featured Stations or Search Results
        when (val featuredStationsState = state.featuredStations) {
            is ResourceState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResourceState.Success -> {
                if (featuredStationsState.data.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.isSearchActive) 
                                "No stations found for '${state.searchQuery}'" 
                            else 
                                "No featured stations available",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(featuredStationsState.data) { station ->
                            FeaturedStationItem(
                                station = station,
                                onClick = { 
                                    radioPlayerService?.playStation(station)
                                    viewModel.addToRecentStations(station)
                                    onStationClick(station.id)
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                }
            }
            is ResourceState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = featuredStationsState.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Only show categories if not in search mode
        if (!state.isSearchActive) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Browse by Categories
            Text(
                text = "Browse By Category",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Categories Grid (2x2)
            val categories = state.categories
            if (categories.size >= 4) {
                // First row
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryItem(
                        category = categories[0],
                        color = Color(0xFF6C63FF).copy(alpha = 0.6f),
                        onClick = { /* Navigate to category */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp, bottom = 8.dp)
                    )
                    
                    CategoryItem(
                        category = categories[1],
                        color = Color(0xFFFF6584).copy(alpha = 0.6f),
                        onClick = { /* Navigate to category */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, bottom = 8.dp)
                    )
                }
                
                // Second row
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryItem(
                        category = categories[2],
                        color = Color(0xFFFFC75F).copy(alpha = 0.6f),
                        onClick = { /* Navigate to category */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp, top = 8.dp)
                    )
                    
                    CategoryItem(
                        category = categories[3],
                        color = Color(0xFF845EC2).copy(alpha = 0.6f),
                        onClick = { /* Navigate to category */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, top = 8.dp)
                    )
                }
            }
        }
        
        // Recently Played
        if (state.recentStations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Recently Played",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Recent Stations List
            state.recentStations.forEachIndexed { index, station ->
                RecentStationItem(
                    station = station,
                    onClick = {
                        radioPlayerService?.playStation(station)
                        viewModel.addToRecentStations(station)
                        onStationClick(station.id)
                    }
                )
                if (index < state.recentStations.size - 1) {
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
        
        // Now Playing Card would appear at the bottom when a station is playing
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
    }
}


@Composable
fun CategoryItem(
    category: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clickable(onClick = onClick),
        backgroundColor = color,
        elevation = 2.dp,
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = category,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecentStationItem(
    station: RadioStation,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = station.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}