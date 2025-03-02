package com.sync.filesyncmanager

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sync.filesyncmanager.screens.BrowseScreen
import com.sync.filesyncmanager.screens.FavoritesScreen
import com.sync.filesyncmanager.screens.HomeScreen
import com.sync.filesyncmanager.screens.NowPlayingScreen
import com.sync.filesyncmanager.screens.ProfileScreen
import com.sync.filesyncmanager.screens.StationDetailScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Browse : Screen("browse", "Browse", Icons.Filled.Radio)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.FavoriteBorder)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    
    // Additional routes without bottom navigation
    object StationDetail : Screen("station/{stationId}", "Station", Icons.Filled.Radio) {
        fun createRoute(stationId: String): String = "station/$stationId"
    }
    
    object NowPlaying : Screen("now_playing", "Now Playing", Icons.Filled.Radio)
}

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Browse,
        Screen.Favorites,
        Screen.Profile
    )
    
    val radioPlayerService = LocalRadioPlayerService.current
    
    MaterialTheme {
        Scaffold(
            bottomBar = {
                // Only show bottom navigation when we're on a main screen
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isMainScreen = items.any { it.route == currentRoute }
                
                if (isMainScreen) {
                    BottomNavigation {
                        val currentDestination = navBackStackEntry?.destination
                        
                        items.forEach { screen ->
                            BottomNavigationItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { 
                    HomeScreen(
                        onStationClick = { stationId ->
                            navController.navigate(Screen.StationDetail.createRoute(stationId))
                        }
                    ) 
                }
                
                composable(Screen.Browse.route) { 
                    BrowseScreen(
                        onStationClick = { stationId ->
                            navController.navigate(Screen.StationDetail.createRoute(stationId))
                        }
                    ) 
                }
                
                composable(Screen.Favorites.route) { 
                    FavoritesScreen(
                        onStationClick = { stationId ->
                            navController.navigate(Screen.StationDetail.createRoute(stationId))
                        }
                    ) 
                }
                
                composable(Screen.Profile.route) { 
                    ProfileScreen() 
                }
                
                // Station Detail Route
                composable(
                    route = Screen.StationDetail.route,
                    arguments = listOf(navArgument("stationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val stationId = backStackEntry.arguments?.getString("stationId") ?: "1"
                    
                    StationDetailScreen(
                        stationId = stationId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onPlayClick = { station ->
                            radioPlayerService?.playStation(station)
                            navController.navigate("now_playing/${station.id}")
                        }
                    )
                }
                
                // Now Playing Route
                composable(
                    route = "now_playing/{stationId}",
                    arguments = listOf(navArgument("stationId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val stationId = backStackEntry.arguments?.getString("stationId") ?: "1"
                    
                    NowPlayingScreen(
                        stationId = stationId,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}