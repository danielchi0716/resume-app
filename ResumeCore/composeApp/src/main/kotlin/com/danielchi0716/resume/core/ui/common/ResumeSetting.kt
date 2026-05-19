package com.danielchi0716.resume.core.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.danielchi0716.resume.core.model.Url

data class ResumeSetting(
    val repoUrl: String,
    val dataHost: String,
    val shareUrl: String,
) {
    fun resolveUrl(url: Url): String {
        val raw = url.raw
        if (raw.startsWith("http://") || raw.startsWith("https://")) return raw
        return "https://$dataHost/${raw.trimStart('/')}"
    }
}

val LocalResumeSetting = staticCompositionLocalOf<ResumeSetting> {
    error("ResumeSetting not provided. Wrap your composition in CompositionLocalProvider(LocalResumeSetting provides ...).")
}
