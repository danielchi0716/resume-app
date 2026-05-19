@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.SkillId
import com.danielchi0716.resume.core.ui.common.UiState
import com.danielchi0716.resume.core.ui.common.ErrorState
import com.danielchi0716.resume.core.ui.common.LoadingState
import com.danielchi0716.resume.core.ui.common.ResumeTopAppBar
import com.danielchi0716.resume.core.ui.common.rememberDataLocale

@Composable
fun SkillsScreen() {
    val locale = rememberDataLocale()
    val viewModel: SkillsViewModel = hiltViewModel(key = locale.code)
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ResumeTopAppBar(
            title = stringResource(R.string.title_skills),
            leadingIcon = Icons.Filled.Code,
        )
        when (val s = uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::retry)
            is UiState.Ready -> SkillsContent(skills = s.data)
        }
    }
}

@Composable
private fun SkillsContent(skills: List<Skill>) {
    var filter by rememberSaveable { mutableStateOf("all") }

    val typesPresent = remember(skills) {
        skills.map { typeKey(it) }.toSet().toList()
    }
    val labelAll = stringResource(R.string.filter_all)
    val labelPlatform = stringResource(R.string.filter_platform)
    val labelCategory = stringResource(R.string.filter_category)
    val filters = buildList {
        add("all" to labelAll)
        if ("platform" in typesPresent) add("platform" to labelPlatform)
        if ("category" in typesPresent) add("category" to labelCategory)
    }
    val visible = skills.filter { filter == "all" || typeKey(it) == filter }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item {
            FilterRow(
                filters = filters,
                selected = filter,
                onSelect = { filter = it },
            )
        }
        visible.forEachIndexed { idx, skill ->
            item(key = skill.id) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    SkillCard(skill = skill, paletteIdx = idx)
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    filters: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        filters.forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        }
    }
}

private data class SkillPalette(val container: Color, val onContainer: Color, val accent: Color)

@Composable
private fun palette(idx: Int): SkillPalette {
    val cs = MaterialTheme.colorScheme
    return when (idx % 3) {
        0 -> SkillPalette(cs.primaryContainer, cs.onPrimaryContainer, cs.primary)
        1 -> SkillPalette(cs.tertiaryContainer, cs.onTertiaryContainer, cs.tertiary)
        else -> SkillPalette(cs.secondaryContainer, cs.onSecondaryContainer, cs.secondary)
    }
}

@Composable
private fun SkillCard(skill: Skill, paletteIdx: Int) {
    val p = palette(paletteIdx)
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = p.container,
                    contentColor = p.onContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = iconForSkill(skill.id),
                            contentDescription = null,
                        )
                    }
                }
                Column {
                    Text(
                        text = stringResource(nameResFor(skill.id)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = if (skill is Skill.Platform) {
                            stringResource(R.string.skill_type_platform)
                        } else {
                            stringResource(R.string.skill_type_category)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
            when (skill) {
                is Skill.Platform -> {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        skill.subcategories.forEach { sub ->
                            Column {
                                Text(
                                    text = sub.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = p.accent,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    sub.items.forEach { item ->
                                        SkillTag(label = item, accent = p.accent)
                                    }
                                }
                            }
                        }
                    }
                }
                is Skill.Category -> {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        skill.tags.forEach { tag ->
                            SkillTag(label = tag, accent = p.accent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillTag(label: String, accent: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(modifier = Modifier.size(6.dp).background(accent, CircleShape))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

private fun typeKey(skill: Skill): String = when (skill) {
    is Skill.Platform -> "platform"
    is Skill.Category -> "category"
}

private fun iconForSkill(id: SkillId): ImageVector = when (id) {
    SkillId.AI_LLM -> Icons.Filled.AutoAwesome
    SkillId.ARCHITECTURE -> Icons.Filled.Architecture
    SkillId.KMP -> Icons.Filled.Hub
    SkillId.ANDROID -> Icons.Filled.Android
    SkillId.IOS -> Icons.Filled.PhoneIphone
    SkillId.TOOLS -> Icons.Filled.Build
}

private fun nameResFor(id: SkillId): Int = when (id) {
    SkillId.AI_LLM -> R.string.skill_name_ai_llm
    SkillId.ARCHITECTURE -> R.string.skill_name_architecture
    SkillId.KMP -> R.string.skill_name_kmp
    SkillId.ANDROID -> R.string.skill_name_android
    SkillId.IOS -> R.string.skill_name_ios
    SkillId.TOOLS -> R.string.skill_name_tools
}
