@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.danielchi0716.resume.core.data.AppPreferences
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.common.AppActions
import com.danielchi0716.resume.core.ui.common.LocalAppActions
import com.danielchi0716.resume.core.ui.more.MoreScreen
import com.danielchi0716.resume.core.ui.profile.ProfileScreen
import com.danielchi0716.resume.core.ui.skills.SkillsScreen
import com.danielchi0716.resume.core.ui.theme.ResumeTheme
import com.danielchi0716.resume.core.ui.theme.ThemeMode
import com.danielchi0716.resume.core.ui.work.WorkScreen
import kotlinx.coroutines.launch

private val ResumeUrl: String = BuildConfig.RESUME_SHARE_URL

private enum class Tab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Profile(R.string.tab_profile, Icons.Filled.Person),
    Work(R.string.tab_work, Icons.Filled.Work),
    Skills(R.string.tab_skills, Icons.Filled.Code),
    More(R.string.tab_more, Icons.Filled.Apps),
}

@Preview
@Composable
fun App() {
    val context = LocalContext.current
    val prefs = remember(context) { AppPreferences(context) }
    var themeMode by remember { mutableStateOf(prefs.themeMode) }

    ResumeTheme(themeMode = themeMode) {
        ResumeRoot(
            onThemeChange = {
                themeMode = it
                prefs.themeMode = it
            },
        )
    }
}

private fun applyAppLocale(locale: Locale) {
    val tag = when (locale) {
        Locale.TraditionalChinese -> "zh"
        Locale.English -> "en"
    }
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
}

@Composable
private fun ResumeRoot(
    onThemeChange: (ThemeMode) -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(Tab.Profile) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val shareTitle = stringResource(R.string.share_title)
    val shareText = stringResource(R.string.share_text)
    val shareCopied = stringResource(R.string.share_copied)

    val handleShare: () -> Unit = {
        val sent = sendShareIntent(context, shareTitle, shareText, ResumeUrl)
        if (!sent) {
            copyToClipboard(context, ResumeUrl)
            scope.launch { snackbar.showSnackbar(shareCopied) }
        }
    }

    val appActions = remember(onThemeChange) {
        AppActions(
            onLocaleChange = ::applyAppLocale,
            onThemeChange = onThemeChange,
            onShare = handleShare,
        )
    }

    CompositionLocalProvider(LocalAppActions provides appActions) {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Tab.entries.forEach { t ->
                        NavigationBarItem(
                            selected = tab == t,
                            onClick = { tab = t },
                            icon = { Icon(t.icon, contentDescription = null) },
                            label = { Text(stringResource(t.labelRes)) },
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbar) },
            containerColor = MaterialTheme.colorScheme.surface,
            // Each screen owns its own TopAppBar (and that bar handles its own
            // status-bar inset), so we must zero out Scaffold's content insets to
            // avoid double counting the top inset.
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (tab) {
                    Tab.Profile -> ProfileScreen()
                    Tab.Work -> WorkScreen()
                    Tab.Skills -> SkillsScreen()
                    Tab.More -> MoreScreen(resumeUrl = ResumeUrl)
                }
            }
        }
    }
}

private fun sendShareIntent(context: Context, title: String, text: String, url: String): Boolean {
    return runCatching {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$text\n$url")
        }
        val chooser = Intent.createChooser(send, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
        true
    }.getOrElse { false }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText("resume", text))
}
