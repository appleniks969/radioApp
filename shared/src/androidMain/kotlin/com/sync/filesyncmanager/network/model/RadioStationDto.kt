package com.sync.filesyncmanager.network.model

import com.google.gson.annotations.SerializedName
import com.sync.filesyncmanager.RadioStation

/**
 * Data Transfer Object for the Radio Browser API station response
 */
data class RadioStationDto(
    @SerializedName("stationuuid")
    val uuid: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("url")
    val streamUrl: String,
    
    @SerializedName("favicon")
    val favicon: String,
    
    @SerializedName("tags")
    val tags: String,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("language")
    val language: String,
    
    @SerializedName("votes")
    val votes: Int,
    
    @SerializedName("codec")
    val codec: String,
    
    @SerializedName("bitrate")
    val bitrate: Int,
    
    @SerializedName("clickcount")
    val clickCount: Int,
    
    @SerializedName("homepage")
    val homepage: String
) {
    /**
     * Convert the DTO to a domain model
     */
    fun toDomainModel(): RadioStation {
        val primaryTag = tags.split(",").firstOrNull()?.trim() ?: "Unknown"
        
        // Generate a description using available data
        val description = buildString {
            append("$name is a ")
            if (language.isNotBlank()) append("$language ")
            append("radio station ")
            if (country.isNotBlank()) append("from $country ")
            if (bitrate > 0) append("streaming at ${bitrate}kbps")
        }
        
        return RadioStation(
            id = uuid,
            name = name,
            streamUrl = streamUrl,
            imageUrl = favicon.takeIf { it.isNotBlank() } ?: "https://picsum.photos/200",
            genre = primaryTag.capitalize(),
            description = description
        )
    }
}

private fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this[0].uppercase() + this.substring(1).lowercase()
    } else {
        this
    }
}