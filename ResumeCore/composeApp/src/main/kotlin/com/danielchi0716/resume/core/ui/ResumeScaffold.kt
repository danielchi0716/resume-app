@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.danielchi0716.resume.core.R

internal enum class Tab(@param:StringRes val labelRes: Int, val icon: ImageVector) {
    Profile(R.string.tab_profile, Icons.Filled.Person),
    Work(R.string.tab_work, Icons.Filled.Work),
    Skills(R.string.tab_skills, Icons.Filled.Code),
    More(R.string.tab_more, Icons.Filled.Apps),
}

@Composable
internal fun ResumeScaffold(
    snackbar: SnackbarHostState,
    selectedTab: Tab,
    onTabChange: (Tab) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onTabChange(tab) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
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
