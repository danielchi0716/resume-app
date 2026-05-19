@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui.screen.more

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hub
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.ui.common.UiState
import com.danielchi0716.resume.core.ui.common.ContactQuickRow
import com.danielchi0716.resume.core.ui.common.ErrorState
import com.danielchi0716.resume.core.ui.common.LoadingState
import com.danielchi0716.resume.core.ui.common.LocalResumeSetting
import com.danielchi0716.resume.core.ui.common.ResumeTopAppBar
import com.danielchi0716.resume.core.ui.common.SectionLabel
import com.danielchi0716.resume.core.ui.common.TinyChip
import com.danielchi0716.resume.core.ui.common.rememberDataLocale
import com.danielchi0716.resume.core.ui.format.formatPeriod

@Composable
fun MoreScreen() {
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
            is UiState.Ready -> MoreContent(data = s.data)
        }
    }
}

@Composable
private fun MoreContent(data: MoreData) {
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
                CtaCard()
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
private fun CtaCard() {
    val uri = LocalUriHandler.current
    val setting = LocalResumeSetting.current
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
            KmpArchitectureBlock(
                onClick = { runCatching { uri.openUri(setting.repoUrl) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            )
            WebRowCard(
                onClick = { runCatching { uri.openUri(setting.shareUrl) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
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
private fun KmpArchitectureBlock(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.clickable { onClick() },
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Hub,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Column {
                        Text(
                            text = "Kotlin Multiplatform",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.sp,
                            ),
                        )
                        Text(
                            text = "Shared Domain · Data · Networking",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .alpha(0.85f),
                        )
                    }
                }
            }
            KmpConnectorCanvas(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = 6.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                PlatformCircle(
                    icon = Icons.Filled.Android,
                    label = "Android",
                    desc = stringResource(R.string.platform_android_desc),
                )
                PlatformCircle(
                    icon = Icons.Filled.PhoneIphone,
                    label = "iOS",
                    desc = stringResource(R.string.platform_ios_desc),
                )
            }
        }
    }
}

@Composable
private fun KmpConnectorCanvas(
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        // x=70 / x=250 in viewBox 320 → 21.875% / 78.125% — keeps the
        // dashed lines aligned with the platform circles below
        val leftX = w * (70f / 320f)
        val rightX = w * (250f / 320f)
        val midY = h * (14f / 40f)
        val stroke = 1.5.dp.toPx()
        val dotR = 3.5.dp.toPx()
        val dash = floatArrayOf(3.dp.toPx(), 3.dp.toPx())
        val effect = PathEffect.dashPathEffect(dash, 0f)

        val leftPath = Path().apply {
            moveTo(centerX, 0f)
            lineTo(centerX, midY)
            lineTo(leftX, midY)
            lineTo(leftX, h)
        }
        val rightPath = Path().apply {
            moveTo(centerX, 0f)
            lineTo(centerX, midY)
            lineTo(rightX, midY)
            lineTo(rightX, h)
        }
        drawPath(leftPath, color, style = Stroke(width = stroke, pathEffect = effect))
        drawPath(rightPath, color, style = Stroke(width = stroke, pathEffect = effect))
        drawCircle(color, dotR, Offset(centerX, 0f))
        drawCircle(color, dotR, Offset(leftX, h))
        drawCircle(color, dotR, Offset(rightX, h))
    }
}

@Composable
private fun PlatformCircle(
    icon: ImageVector,
    label: String,
    desc: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = CircleShape,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(56.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun WebRowCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Web",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.platform_web_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun iconForSideProject(id: String): ImageVector = when (id) {
    "android-album-sdk" -> Icons.Filled.PhotoLibrary
    else -> Icons.Filled.AutoAwesome
}
