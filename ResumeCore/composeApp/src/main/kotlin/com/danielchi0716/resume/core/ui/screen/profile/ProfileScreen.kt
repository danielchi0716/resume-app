package com.danielchi0716.resume.core.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.ResumeCore
import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.LanguageCode
import com.danielchi0716.resume.core.model.LanguageLevel
import com.danielchi0716.resume.core.model.LanguageSkill
import com.danielchi0716.resume.core.model.Photo
import com.danielchi0716.resume.core.resolveUrl
import com.danielchi0716.resume.core.ui.common.ContactQuickRow
import com.danielchi0716.resume.core.ui.common.UiStateContent
import com.danielchi0716.resume.core.ui.common.ResumeTopAppBar
import com.danielchi0716.resume.core.ui.common.SectionLabel
import com.danielchi0716.resume.core.ui.common.rememberDataLocale
import com.danielchi0716.resume.core.ui.format.formatPeriod

@Composable
fun ProfileScreen() {
    val locale = rememberDataLocale()
    val viewModel: ProfileViewModel = hiltViewModel(key = locale.code)
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ResumeTopAppBar(
            title = stringResource(R.string.title_profile),
            leadingIcon = Icons.Filled.Person,
        )
        UiStateContent(state = uiState, onRetry = viewModel::retry) { data ->
            ProfileContent(data = data)
        }
    }
}

@Composable
private fun ProfileContent(data: ProfileData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item { HeroCard(header = data.header) }
        item {
            Box(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                ContactQuickRow(contacts = data.header.contacts.take(4))
            }
        }
        item { LanguagesSection(languages = data.languages) }
        item { EducationSection(education = data.education) }
        item { AboutSection(paragraphs = data.about) }
    }
}

@Composable
private fun HeroCard(header: Header) {
    val colorScheme = MaterialTheme.colorScheme
    val brush = Brush.linearGradient(
        colors = listOf(
            colorScheme.primaryContainer,
            colorScheme.secondaryContainer,
            colorScheme.tertiaryContainer,
        ),
    )

    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .background(brush)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarImage(
                        photo = header.photo,
                        monogram = header.monogram,
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape),
                    )
                    Column {
                        Text(
                            text = header.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = header.englishName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                        )
                    }
                }
                Column {
                    Text(
                        text = header.subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = header.tagline.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    header.tagline.keywords.forEach { keyword ->
                        Surface(
                            color = Color.White.copy(alpha = 0.45f),
                            contentColor = colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = keyword,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSection(paragraphs: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionLabel(text = stringResource(R.string.section_about), modifier = Modifier.padding(bottom = 12.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                val visible = if (expanded) paragraphs else paragraphs.take(1)
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    visible.forEach { p ->
                        Text(
                            text = p,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                if (paragraphs.size > 1) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                        )
                        Text(
                            text = if (expanded) stringResource(R.string.action_collapse) else stringResource(R.string.action_expand),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguagesSection(languages: List<Language>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionLabel(text = stringResource(R.string.section_languages), modifier = Modifier.padding(bottom = 12.dp))
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            languages.forEachIndexed { i, l ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = if (l.level is LanguageLevel.Native) {
                                    Icons.Filled.Translate
                                } else {
                                    Icons.Filled.Language
                                },
                                contentDescription = null,
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(l.code.displayNameRes),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = l.level.display(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    l.badge?.let { badge ->
                        PremiumBadge(text = badge)
                    }
                }
                if (i < languages.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 84.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun EducationSection(education: List<Education>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionLabel(text = stringResource(R.string.section_education), modifier = Modifier.padding(bottom = 12.dp))
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            education.forEachIndexed { i, e ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Surface(
                        color = if (i == 0) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (i == 0) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Filled.School,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = e.school,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = e.major,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                        Text(
                            text = formatPeriod(e.period),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
                if (i < education.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 74.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarImage(
    photo: Photo,
    monogram: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val resolvedUrl = remember(photo.url) { ResumeCore.resolveUrl(photo.url) }
    val request = remember(resolvedUrl) {
        ImageRequest.Builder(context)
            .data(resolvedUrl)
            .crossfade(true)
            .build()
    }
    Box(
        modifier = modifier.clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            model = request,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = { MonogramFallback(monogram) },
            error = { MonogramFallback(monogram) },
        )
    }
}

@Composable
private fun MonogramFallback(monogram: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = monogram,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

private val GoldStart = Color(0xFFC8A24A)
private val GoldEnd = Color(0xFFB0863A)
private val GoldText = Color(0xFFFFFBE8)

@Composable
private fun PremiumBadge(text: String) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .height(26.dp)
            .shadow(elevation = 1.dp, shape = shape, clip = false)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(GoldStart, GoldEnd),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
                shape = shape,
            )
            .border(
                width = 0.5.dp,
                color = GoldEnd.copy(alpha = 0.5f),
                shape = shape,
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = null,
            tint = GoldText,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            color = GoldText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
    }
}

private val LanguageCode.displayNameRes: Int
    get() = when (this) {
        LanguageCode.ZH -> R.string.language_zh
        LanguageCode.EN -> R.string.language_en
    }

private val LanguageSkill.labelRes: Int
    get() = when (this) {
        LanguageSkill.BASIC        -> R.string.language_skill_basic
        LanguageSkill.INTERMEDIATE -> R.string.language_skill_intermediate
        LanguageSkill.ADVANCED     -> R.string.language_skill_advanced
        LanguageSkill.FLUENT       -> R.string.language_skill_fluent
    }

@Composable
private fun LanguageLevel.display(): String = when (this) {
    LanguageLevel.Native -> stringResource(R.string.language_level_native)
    is LanguageLevel.Proficiency -> {
        val l = "${stringResource(R.string.language_skill_listening)} ${stringResource(listening.labelRes)}"
        val s = "${stringResource(R.string.language_skill_speaking)} ${stringResource(speaking.labelRes)}"
        val r = "${stringResource(R.string.language_skill_reading)} ${stringResource(reading.labelRes)}"
        val w = "${stringResource(R.string.language_skill_writing)} ${stringResource(writing.labelRes)}"
        "$l · $s · $r · $w"
    }
}
