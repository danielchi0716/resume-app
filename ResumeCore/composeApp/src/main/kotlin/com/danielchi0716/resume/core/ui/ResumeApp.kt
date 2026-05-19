package com.danielchi0716.resume.core.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.danielchi0716.resume.core.AppViewModel
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.common.AppActions
import com.danielchi0716.resume.core.ui.common.LocalAppActions
import com.danielchi0716.resume.core.ui.common.LocalResumeSetting
import com.danielchi0716.resume.core.ui.screen.more.MoreScreen
import com.danielchi0716.resume.core.ui.screen.profile.ProfileScreen
import com.danielchi0716.resume.core.ui.screen.skills.SkillsScreen
import com.danielchi0716.resume.core.ui.screen.work.WorkScreen
import com.danielchi0716.resume.core.ui.theme.ResumeTheme
import kotlinx.coroutines.launch

@Preview
@Composable
fun ResumeApp() {
    val appVm: AppViewModel = hiltViewModel()
    val themeMode by appVm.themeMode.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val shareUrl = appVm.setting.shareUrl
    val shareTitle = stringResource(R.string.share_title)
    val shareText = stringResource(R.string.share_text)
    val shareCopied = stringResource(R.string.share_copied)

    val appActions = remember(appVm) {
        AppActions(
            onLocaleChange = ::applyAppLocale,
            onThemeChange = appVm::setThemeMode,
            onShare = {
                val sent = sendShareIntent(context, shareTitle, shareText, shareUrl)
                if (!sent) {
                    copyToClipboard(context, shareUrl)
                    scope.launch { snackbar.showSnackbar(shareCopied) }
                }
            },
        )
    }

    var tab by rememberSaveable { mutableStateOf(Tab.Profile) }

    CompositionLocalProvider(
        LocalResumeSetting provides appVm.setting,
        LocalAppActions provides appActions,
    ) {
        ResumeTheme(themeMode = themeMode) {
            ResumeScaffold(
                snackbar = snackbar,
                selectedTab = tab,
                onTabChange = { tab = it },
            ) { padding ->
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                    when (tab) {
                        Tab.Profile -> ProfileScreen()
                        Tab.Work -> WorkScreen()
                        Tab.Skills -> SkillsScreen()
                        Tab.More -> MoreScreen()
                    }
                }
            }
        }
    }
}

private fun applyAppLocale(locale: Locale) {
    val tag = when (locale) {
        Locale.TraditionalChinese -> "zh"
        Locale.English -> "en"
    }
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
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
