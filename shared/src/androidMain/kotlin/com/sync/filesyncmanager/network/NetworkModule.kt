package com.sync.filesyncmanager.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module for setting up Retrofit and OkHttp
 */
object NetworkModule {
    
    // Default timeout values
    private const val CONNECT_TIMEOUT = 15L
    private const val WRITE_TIMEOUT = 15L
    private const val READ_TIMEOUT = 15L
    
    /**
     * Get a random API server from the Radio Browser API
     * This helps load balance and prevents overloading a single server
     */
    private fun getRandomServer(): String {
        val servers = listOf(
            "de1.api.radio-browser.info",
            "fr1.api.radio-browser.info",
            "nl1.api.radio-browser.info"
        )
        return servers.random()
    }
    
    /**
     * Create and configure OkHttpClient
     */
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }
    
    /**
     * Create the RadioBrowserApi implementation
     */
    fun provideRadioBrowserApi(): RadioBrowserApi {
        val baseUrl = "https://${getRandomServer()}/"
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RadioBrowserApi::class.java)
    }
}