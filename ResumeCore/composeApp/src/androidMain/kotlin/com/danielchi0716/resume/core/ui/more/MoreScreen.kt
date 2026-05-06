@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.ui.common.UiState
import com.danielchi0716.resume.core.ui.common.ContactQuickRow
import com.danielchi0716.resume.core.ui.common.ErrorState
import com.danielchi0716.resume.core.ui.common.LoadingState
import com.danielchi0716.resume.core.ui.common.ResumeTopAppBar
import com.danielchi0716.resume.core.ui.common.SectionLabel
import com.danielchi0716.resume.core.ui.common.TinyChip
import com.danielchi0716.resume.core.ui.common.rememberDataLocale
import com.danielchi0716.resume.core.ui.format.formatPeriod

@Composable
fun MoreScreen(resumeUrl: String) {
    val locale = rememberDataLocale()
    val viewModel: MoreViewModel = hiltViewModel(key = locale.code)
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ResumeTopAppBar(
            title = stringResource(R.string.title_more),
            leadingIcon = Icons.Filled.Apps,
        )
        when (val s = uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::retry)
            is UiState.Ready -> MoreContent(data = s.data, resumeUrl = resumeUrl)
        }
    }
}

@Composable
private fun MoreContent(data: MoreData, resumeUrl: String) {
    val openMap = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item {
            SectionLabel(
                text = stringResource(R.string.section_side_projects),
                modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            )
        }
        data.sideProjects.forEach { p ->
            item(key = p.id) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    SideProjectCard(
                        project = p,
                        open = openMap[p.id] == true,
                        onToggle = { openMap[p.id] = !(openMap[p.id] ?: false) },
                    )
                }
            }
        }
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                CtaCard(resumeUrl = resumeUrl)
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionLabel(
                    text = stringResource(R.string.section_contact),
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                ContactQuickRow(contacts = data.header.contacts.take(4))
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "v${data.meta.version} · ${data.meta.updatedAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun SideProjectCard(
    project: SideProject,
    open: Boolean,
    onToggle: () -> Unit,
) {
    val rotation by animateFloatAsState(if (open) 180f else 0f, label = "chevron")
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = iconForSideProject(project.id),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = project.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TinyChip(text = formatPeriod(project.period))
                }
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotation),
                )
            }
            AnimatedVisibility(
                visible = open,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 0.dp).padding(bottom = 18.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 14.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        project.bullets.forEach { b ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(top = 3.dp)
                                        .size(16.dp),
                                )
                                Text(
                                    text = b,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CtaCard(resumeUrl: String) {
    val uri = LocalUriHandler.current
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = stringResource(R.string.cta_thanks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = stringResource(R.string.cta_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PlatformCard(
                    label = "Android",
                    desc = stringResource(R.string.platform_android_desc),
                    icon = Icons.Filled.Android,
                    enabled = true,
                    onClick = null,
                    modifier = Modifier.weight(1f),
                )
                PlatformCard(
                    label = "iOS",
                    desc = stringResource(R.string.platform_ios_desc),
                    icon = Icons.Filled.PhoneIphone,
                    enabled = false,
                    onClick = null,
                    modifier = Modifier.weight(1f),
                )
                PlatformCard(
                    label = "Web",
                    desc = stringResource(R.string.platform_web_desc),
                    icon = Icons.Filled.Language,
                    enabled = true,
                    onClick = { runCatching { uri.openUri(resumeUrl) } },
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = stringResource(R.string.cta_bridge),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp),
            )
        }
    }
}

@Composable
private fun PlatformCard(
    label: String,
    desc: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val alpha = if (enabled) 1f else 0.55f
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .height(112.dp)
            .let { if (enabled && onClick != null) it.clickable { onClick() } else it },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = alpha),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape,
                modifier = Modifier.size(38.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun iconForSideProject(id: String): ImageVector = when (id) {
    "android-album-sdk" -> Icons.Filled.PhotoLibrary
    else -> Icons.Filled.AutoAwesome
}
