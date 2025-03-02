package com.sync.filesyncmanager.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
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
import com.sync.filesyncmanager.LocalRadioRepository
import com.sync.filesyncmanager.LocalViewModelFactory
import com.sync.filesyncmanager.RadioStation
import com.sync.filesyncmanager.components.RadioStationItem
import com.sync.filesyncmanager.utils.ColorUtils.getRandomColorForStation
import com.sync.filesyncmanager.utils.ColorUtils.getCountryForStation
import com.sync.filesyncmanager.viewmodel.BrowseMode
import com.sync.filesyncmanager.viewmodel.BrowseViewModel
import com.sync.filesyncmanager.viewmodel.ResourceState

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun BrowseScreen(
    onStationClick: (String) -> Unit = {}
) {
    // Get the ViewModel factory from the CompositionLocal
    val factory = LocalViewModelFactory.current
    
    // Create the ViewModel using the factory
    val viewModel: BrowseViewModel = viewModel(factory = factory!!)
    
    // Get the current state from the ViewModel
    val state by viewModel.state.collectAsState()
    
    val radioPlayerService = LocalRadioPlayerService.current
    val radioRepository = LocalRadioRepository.current
    
    // Local state for the search query
    var searchQuery by remember { mutableStateOf(state.searchQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Browse Stations",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Modern Search Bar with Icon
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Browse Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrowseModeButton(
                text = "Categories",
                isSelected = state.browseMode == BrowseMode.CATEGORIES,
                onClick = { viewModel.setBrowseMode(BrowseMode.CATEGORIES) }
            )
            
            BrowseModeButton(
                text = "Countries",
                isSelected = state.browseMode == BrowseMode.COUNTRIES,
                onClick = { viewModel.setBrowseMode(BrowseMode.COUNTRIES) }
            )
            
            BrowseModeButton(
                text = "Most Popular",
                isSelected = state.browseMode == BrowseMode.BY_CLICKS,
                onClick = { viewModel.setBrowseMode(BrowseMode.BY_CLICKS) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (state.browseMode) {
            BrowseMode.CATEGORIES -> {
                // Category Filters
                Text(
                    text = "Browse By Category",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 4
                ) {
                    // Add "All" option at the beginning
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.loadTopStations()
                            },
                        color = if (state.selectedCategory == null) 
                                MaterialTheme.colors.primary 
                            else 
                                Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = if (state.selectedCategory == null) 2.dp else 0.dp
                    ) {
                        Text(
                            text = "All",
                            color = if (state.selectedCategory == null) 
                                    Color.White 
                                else 
                                    Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    // Display categories from state
                    state.categories.forEach { category ->
                        val isSelected = category == state.selectedCategory
                        
                        Surface(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.loadStationsByCategory(category)
                                },
                            color = if (isSelected) 
                                    MaterialTheme.colors.primary 
                                else 
                                    Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp),
                            elevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            BrowseMode.COUNTRIES -> {
                // Country Filters
                Text(
                    text = "Browse By Country",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display countries based on the state
                when (val countriesState = state.countries) {
                    is ResourceState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                    is ResourceState.Success -> {
                        val countries = countriesState.data
                        
                        if (countries.isEmpty()) {
                            Text(
                                text = "No countries available",
                                color = Color.Gray
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(countries) { country ->
                                    val isSelected = country == state.selectedCountry
                                    
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                viewModel.loadStationsByCountry(country)
                                            },
                                        color = if (isSelected) 
                                                MaterialTheme.colors.primary 
                                            else 
                                                Color.LightGray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = if (isSelected) 2.dp else 0.dp
                                    ) {
                                        Text(
                                            text = country,
                                            color = if (isSelected) Color.White else Color.Black,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is ResourceState.Error -> {
                        Text(
                            text = countriesState.message,
                            color = Color.Red
                        )
                    }
                }
            }
            BrowseMode.BY_CLICKS -> {
                // Popular stations by click - no additional filters needed
                Text(
                    text = "Most Popular Stations",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Showing stations ranked by listener popularity",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display stations based on their state
        when (val stationsState = state.stations) {
            is ResourceState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResourceState.Success -> {
                val stations = stationsState.data
                
                // Search Results Counter
                if (state.searchQuery.isNotEmpty() || state.selectedCategory != null) {
                    Text(
                        text = "${stations.size} stations found",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (stations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.searchQuery.isNotEmpty())
                                "No stations found for '${state.searchQuery}'"
                            else if (state.selectedCategory != null)
                                "No stations found in category '${state.selectedCategory}'"
                            else
                                "No stations available",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    // Results Display - List View for search results
                    if (state.searchQuery.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(stations) { station ->
                                StationListItem(
                                    station = station,
                                    onClick = { 
                                        radioPlayerService?.playStation(station)
                                        radioRepository?.addToRecentStations(station)
                                        onStationClick(station.id)
                                    },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    } else {
                        // Grid view for browsing
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(stations) { station ->
                                RadioStationItem(
                                    station = station,
                                    onClick = { 
                                        radioPlayerService?.playStation(station)
                                        radioRepository?.addToRecentStations(station)
                                        onStationClick(station.id)
                                    },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
            is ResourceState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stationsState.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StationListItem(
    station: RadioStation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Logo Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getRandomColorForStation(station)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = station.name.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Station Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = station.genre,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Button for selecting different browse modes
 */
@Composable
fun BrowseModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colors.primary else Color.LightGray.copy(alpha = 0.3f),
        shape = RoundedCornerShape(20.dp),
        elevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
            style = MaterialTheme.typography.button,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}