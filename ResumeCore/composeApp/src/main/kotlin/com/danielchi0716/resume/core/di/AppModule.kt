package com.danielchi0716.resume.core.di

import android.content.Context
import androidx.core.app.LocaleManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.danielchi0716.resume.core.BuildConfig
import com.danielchi0716.resume.core.ResumeCore
import com.danielchi0716.resume.core.ResumeService
import com.danielchi0716.resume.core.data.appDataStore
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.common.ResumeSetting
import com.danielchi0716.resume.core.service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import java.util.Locale as JavaLocale

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Each injection returns a service bound to the **current** per-app locale.
     * Combined with `hiltViewModel(key = locale.code)` on the call site, every
     * locale change rebuilds the ViewModel, which rebuilds this binding with the
     * new locale — no need for factories or runtime setLocale calls in ViewModels.
     */
    @Provides
    fun provideResumeService(@ApplicationContext context: Context): ResumeService {
        val locales = LocaleManagerCompat.getApplicationLocales(context)
        val lang = (if (!locales.isEmpty) locales.get(0)?.language else null)
            ?: JavaLocale.getDefault().language
        val locale = if (lang.equals("zh", ignoreCase = true)) Locale.TraditionalChinese else Locale.English
        return ResumeCore.service(locale)
    }

    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.appDataStore

    @Provides
    @Singleton
    fun provideResumeSetting(): ResumeSetting = ResumeSetting(
        repoUrl = BuildConfig.REPO_URL,
        dataHost = BuildConfig.RESUME_DATA_HOST,
        shareUrl = BuildConfig.RESUME_SHARE_URL,
    )
}
