package com.danielchi0716.resume.core.ui.common

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.theme.ThemeMode

/**
 * App-wide write-only callbacks that any TopAppBar overflow menu can invoke.
 * Provided once at the App root and consumed by per-screen TopAppBars.
 *
 * The current locale / theme are read from their own CompositionLocals
 * (system Configuration / [com.danielchi0716.resume.core.ui.theme.LocalThemeMode])
 * so this struct only carries the change-actions.
 */
@Immutable
data class AppActions(
    val onLocaleChange: (Locale) -> Unit,
    val onThemeChange: (ThemeMode) -> Unit,
    val onShare: () -> Unit,
)

val LocalAppActions = staticCompositionLocalOf<AppActions> {
    error("AppActions not provided. Wrap your composition in CompositionLocalProvider(LocalAppActions provides ...).")
}
