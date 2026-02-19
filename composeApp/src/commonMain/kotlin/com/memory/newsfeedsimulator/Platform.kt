package com.memory.newsfeedsimulator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform