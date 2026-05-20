@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui.common

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.ui.common.LocalAppActions
import com.danielchi0716.resume.core.ui.theme.LocalThemeMode
import com.danielchi0716.resume.core.ui.theme.ThemeMode

@Composable
fun ResumeTopAppBar(
    title: String,
    leadingIcon: ImageVector,
) {
    val actions = LocalAppActions.current
    val themeMode = LocalThemeMode.current
    val locale = rememberDataLocale()
    var menuOpen by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = CircleShape,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        },
        actions = {
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null)
                }
                OverflowMenu(
                    open = menuOpen,
                    locale = locale,
                    themeMode = themeMode,
                    onDismiss = { menuOpen = false },
                    onShare = { menuOpen = false; actions.onShare() },
                    onSetLocale = { menuOpen = false; actions.onLocaleChange(it) },
                    onSetTheme = { menuOpen = false; actions.onThemeChange(it) },
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun OverflowMenu(
    open: Boolean,
    locale: Locale,
    themeMode: ThemeMode,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSetLocale: (Locale) -> Unit,
    onSetTheme: (ThemeMode) -> Unit,
) {
    DropdownMenu(expanded = open, onDismissRequest = onDismiss) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.menu_share)) },
            leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) },
            onClick = onShare,
        )
        HorizontalDivider()
        SectionHeading(stringResource(R.string.menu_language))
        RadioMenuItem(
            label = stringResource(R.string.lang_tc),
            icon = Icons.Filled.Translate,
            selected = locale == Locale.TraditionalChinese,
            onClick = { onSetLocale(Locale.TraditionalChinese) },
        )
        RadioMenuItem(
            label = stringResource(R.string.lang_en),
            icon = Icons.Filled.Translate,
            selected = locale == Locale.English,
            onClick = { onSetLocale(Locale.English) },
        )
        HorizontalDivider()
        SectionHeading(stringResource(R.string.menu_theme))
        RadioMenuItem(
            label = stringResource(R.string.theme_system),
            icon = Icons.Filled.SettingsBrightness,
            selected = themeMode == ThemeMode.System,
            onClick = { onSetTheme(ThemeMode.System) },
        )
        RadioMenuItem(
            label = stringResource(R.string.theme_light),
            icon = Icons.Filled.LightMode,
            selected = themeMode == ThemeMode.Light,
            onClick = { onSetTheme(ThemeMode.Light) },
        )
        RadioMenuItem(
            label = stringResource(R.string.theme_dark),
            icon = Icons.Filled.DarkMode,
            selected = themeMode == ThemeMode.Dark,
            onClick = { onSetTheme(ThemeMode.Dark) },
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RadioMenuItem(
                label = stringResource(R.string.theme_dynamic),
                icon = Icons.Filled.AutoAwesome,
                selected = themeMode == ThemeMode.Dynamic,
                onClick = { onSetTheme(ThemeMode.Dynamic) },
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun RadioMenuItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        trailingIcon = {
            Icon(
                imageVector = if (selected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        onClick = onClick,
    )
}
