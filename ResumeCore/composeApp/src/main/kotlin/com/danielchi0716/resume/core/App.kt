@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.common.AppActions
import com.danielchi0716.resume.core.ui.common.LocalAppActions
import com.danielchi0716.resume.core.ui.common.LocalResumeSetting
import com.danielchi0716.resume.core.ui.more.MoreScreen
import com.danielchi0716.resume.core.ui.profile.ProfileScreen
import com.danielchi0716.resume.core.ui.skills.SkillsScreen
import com.danielchi0716.resume.core.ui.theme.ResumeTheme
import com.danielchi0716.resume.core.ui.work.WorkScreen
import kotlinx.coroutines.launch

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

@Composable
private fun ResumeScaffold(
    snackbar: SnackbarHostState,
    selectedTab: Tab,
    onTabChange: (Tab) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = selectedTab == t,
                        onClick = { onTabChange(t) },
                        icon = { Icon(t.icon, contentDescription = null) },
                        label = { Text(stringResource(t.labelRes)) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = content,
    )
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
