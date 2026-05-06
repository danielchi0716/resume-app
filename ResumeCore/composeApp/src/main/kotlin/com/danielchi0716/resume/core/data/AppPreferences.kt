package com.danielchi0716.resume.core.data

import android.content.Context
import androidx.core.content.edit
import com.danielchi0716.resume.core.ui.theme.ThemeMode

/**
 * Single source of truth for persistable app-level user preferences.
 * Backed by SharedPreferences; reads and writes are synchronous and cheap.
 */
class AppPreferences(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(NAME, Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() = prefs.getString(KEY_THEME, null)
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.System
        set(value) {
            prefs.edit { putString(KEY_THEME, value.name) }
        }

    private companion object {
        const val NAME = "resume_app_prefs"
        const val KEY_THEME = "theme_mode"
    }
}
