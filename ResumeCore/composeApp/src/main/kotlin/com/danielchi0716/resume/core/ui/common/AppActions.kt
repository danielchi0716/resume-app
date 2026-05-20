package com.danielchi0716.resume.core.ui.common

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.theme.ThemeMode

@Immutable
data class AppActions(
    val onLocaleChange: (Locale) -> Unit,
    val onThemeChange: (ThemeMode) -> Unit,
    val onShare: () -> Unit,
)

val LocalAppActions = staticCompositionLocalOf<AppActions> {
    error("AppActions not provided. Wrap your composition in CompositionLocalProvider(LocalAppActions provides ...).")
}
