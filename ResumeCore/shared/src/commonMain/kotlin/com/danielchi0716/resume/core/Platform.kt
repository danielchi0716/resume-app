package com.danielchi0716.resume.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform