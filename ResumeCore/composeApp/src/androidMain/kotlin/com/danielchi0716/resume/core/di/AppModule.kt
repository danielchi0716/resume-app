package com.danielchi0716.resume.core.di

import android.content.Context
import androidx.core.app.LocaleManagerCompat
import com.danielchi0716.resume.core.ResumeCore
import com.danielchi0716.resume.core.ResumeService
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}
