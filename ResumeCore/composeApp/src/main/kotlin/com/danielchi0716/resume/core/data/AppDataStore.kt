package com.danielchi0716.resume.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

internal const val APP_DATASTORE_NAME = "resume_app_prefs"

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_DATASTORE_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, APP_DATASTORE_NAME))
    },
)
