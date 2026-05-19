package com.danielchi0716.resume.core

import android.app.Application
import com.danielchi0716.resume.core.ui.common.ResumeSetting
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ResumeApp : Application() {

    @Inject lateinit var setting: ResumeSetting

    override fun onCreate() {
        super.onCreate()
        ResumeEntry.init(NetworkConfig(host = setting.dataHost))
    }
}
