package com.sync.filesyncmanager.service

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.sync.filesyncmanager.RadioStation

class RadioPlayerService(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var currentStation: RadioStation? = null
    
    fun playStation(station: RadioStation) {
        if (currentStation?.id == station.id && player.isPlaying) {
            // Already playing this station, do nothing
            return
        }
        
        currentStation = station
        player.stop()
        player.clearMediaItems()
        
        val mediaItem = MediaItem.fromUri(station.streamUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
    
    fun pausePlayback() {
        if (player.isPlaying) {
            player.pause()
        }
    }
    
    fun resumePlayback() {
        if (!player.isPlaying && currentStation != null) {
            player.play()
        }
    }
    
    fun togglePlayback() {
        if (player.isPlaying) {
            pausePlayback()
        } else {
            resumePlayback()
        }
    }
    
    fun getCurrentStation(): RadioStation? = currentStation
    
    fun isPlaying(): Boolean = player.isPlaying
    
    fun release() {
        player.stop()
        player.release()
    }
}