package com.sync.filesyncmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sync.filesyncmanager.RadioRepository
import com.sync.filesyncmanager.service.RadioPlayerService

/**
 * Factory to create ViewModels
 */
class ViewModelFactory(
    private val repository: RadioRepository,
    private val radioPlayerService: RadioPlayerService? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BrowseViewModel::class.java) -> {
                BrowseViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StationDetailViewModel::class.java) -> {
                StationDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NowPlayingViewModel::class.java) -> {
                NowPlayingViewModel(repository, radioPlayerService) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}