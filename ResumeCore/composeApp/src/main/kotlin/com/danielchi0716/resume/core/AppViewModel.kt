package com.danielchi0716.resume.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.ui.common.ResumeSetting
import com.danielchi0716.resume.core.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val service: ResumeService,
    val setting: ResumeSetting,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = dataStore.data
        .map { prefs ->
            prefs[KEY_THEME]
                ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.System
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeMode.System,
        )

    private val _header = MutableStateFlow<Header?>(null)
    val header: StateFlow<Header?> = _header.asStateFlow()

    init {
        viewModelScope.launch {
            _header.value = runCatching { service.getHeader() }.getOrNull()
        }
    }

    fun setThemeMode(value: ThemeMode) {
        viewModelScope.launch {
            dataStore.edit { it[KEY_THEME] = value.name }
        }
    }

    private companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
    }
}
