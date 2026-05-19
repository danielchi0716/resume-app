package com.danielchi0716.resume.core.ui.common

import androidx.compose.runtime.staticCompositionLocalOf

data class ResumeSetting(
    val repoUrl: String,
    val dataHost: String,
    val shareUrl: String,
)

val LocalResumeSetting = staticCompositionLocalOf<ResumeSetting> {
    error("ResumeSetting not provided. Wrap your composition in CompositionLocalProvider(LocalResumeSetting provides ...).")
}
