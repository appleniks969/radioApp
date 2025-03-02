package com.sync.filesyncmanager.repository

import android.content.Context
import com.sync.filesyncmanager.AndroidRadioRepository
import com.sync.filesyncmanager.RadioRepository
import com.sync.filesyncmanager.network.NetworkModule
import com.sync.filesyncmanager.network.RadioBrowserApi

/**
 * Main app container that holds repository instances
 */
interface AppContainer {
    val radioRepository: RadioRepository
    val radioBrowserApi: RadioBrowserApi
}

/**
 * Default implementation of AppContainer
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    
    override val radioBrowserApi: RadioBrowserApi by lazy {
        NetworkModule.provideRadioBrowserApi()
    }
    
    override val radioRepository: RadioRepository by lazy {
        AndroidRadioRepository(radioBrowserApi, context)
    }
}