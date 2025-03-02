package com.sync.filesyncmanager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform