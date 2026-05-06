package com.danielchi0716.resume.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ResumeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ResumeEntry.init(NetworkConfig(host = BuildConfig.RESUME_DATA_HOST))
    }
}
