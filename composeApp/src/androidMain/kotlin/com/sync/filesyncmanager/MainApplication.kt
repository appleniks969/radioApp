package com.sync.filesyncmanager

import android.app.Application
import com.sync.filesyncmanager.repository.AppContainer
import com.sync.filesyncmanager.repository.DefaultAppContainer

/**
 * Main application class
 */
class MainApplication : Application() {
    // App container instance for dependency injection
    lateinit var container: AppContainer
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize the container with application context
        container = DefaultAppContainer(this)
    }
    
    companion object {
        private var instance: MainApplication? = null
        
        fun getInstance(): MainApplication {
            return instance!!
        }
    }
    
    init {
        instance = this
    }
}