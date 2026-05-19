@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.danielchi0716.resume.core.ui.screen.work

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Project
import com.danielchi0716.resume.core.model.WorkExperience
import com.danielchi0716.resume.core.model.YearItem
import com.danielchi0716.resume.core.ui.common.UiState
import com.danielchi0716.resume.core.ui.common.UiStateContent
import com.danielchi0716.resume.core.ui.common.SectionLabel
import com.danielchi0716.resume.core.ui.common.TinyChip
import com.danielchi0716.resume.core.ui.format.formatDuration
import com.danielchi0716.resume.core.ui.format.formatPeriod
import com.danielchi0716.resume.core.ui.format.yearRangeShort
import kotlinx.coroutines.launch

@Composable
internal fun WorkDetailView(
    uiState: UiState<List<WorkExperience>>,
    onRetry: () -> Unit,
    initialIdx: Int,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    val jobs = (uiState as? UiState.Ready)?.data
    val pagerState = jobs?.let {
        rememberPagerState(
            initialPage = initialIdx.coerceIn(0, (it.size - 1).coerceAtLeast(0)),
            pageCount = { it.size },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DetailTopBar(
            title = stringResource(R.string.work_detail_title),
            counter = pagerState?.let { "${it.currentPage + 1} / ${jobs.size}" },
            onBack = onBack,
        )
        UiStateContent(state = uiState, onRetry = onRetry) { data ->
            WorkDetailContent(jobs = data, pagerState = pagerState!!)
        }
    }
}

@Composable
private fun DetailTopBar(title: String, counter: String?, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (counter != null) {
                Text(
                    text = counter,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun WorkDetailContent(jobs: List<WorkExperience>, pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        JobTabs(
            jobs = jobs,
            pagerState = pagerState,
            onTabClick = { i -> scope.launch { pagerState.animateScrollToPage(i) } },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) { page ->
            JobPage(job = jobs[page], idx = page)
        }
    }
}

@Composable
private fun JobTabs(
    jobs: List<WorkExperience>,
    pagerState: PagerState,
    onTabClick: (Int) -> Unit,
) {
    SecondaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        jobs.forEachIndexed { i, job ->
            val active = pagerState.currentPage == i
            Tab(
                selected = active,
                onClick = { onTabClick(i) },
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = job.company.replace("股份有限公司", "").replace("有限公司", "").trim(),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (active) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                    )
                    Text(
                        text = yearRangeShort(job.period),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun JobPage(job: WorkExperience, idx: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(
                        color = if (idx == 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = if (idx == 0) Icons.Filled.Apartment else Icons.Filled.Business,
                                contentDescription = null,
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = job.company,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TinyChip(text = formatPeriod(job.period))
                            TinyChip(text = formatDuration(job.period))
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    job.bullets.forEach { b ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                Modifier
                                    .padding(top = 8.dp)
                                    .size(6.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                            )
                            Text(
                                text = b,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }

        if (job.projects.isNotEmpty()) {
            SectionLabel(
                text = stringResource(R.string.section_main_projects),
                modifier = Modifier.padding(start = 4.dp),
            )
            job.projects.forEach { p ->
                ProjectExpandableCard(project = p)
            }
        }
    }
}

@Composable
private fun ProjectExpandableCard(project: Project) {
    var open by remember(project.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (open) 180f else 0f, label = "chevron")
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { open = !open }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    val periodLabel = formatPeriod(project.period)
                    val supporting = if (!project.summary.isNullOrBlank()) {
                        "$periodLabel · ${project.summary}"
                    } else {
                        periodLabel
                    }
                    Text(
                        text = supporting,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    when (project) {
                        is Project.YearList -> YearTimeline(project.yearItems)
                        is Project.Bullets -> ProjectBullets(project.bullets)
                    }
                }
            }
        }
    }
}

@Composable
private fun YearTimeline(items: List<YearItem>) {
    Column(modifier = Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { it ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    Text(
                        text = it.years.joinToString(" ─ "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    it.description?.takeIf { d -> d.isNotBlank() }?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (it.bullets.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            it.bullets.forEach { b ->
                                Text(
                                    text = "· $b",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    if (it.tags.isNotEmpty()) {
                        TagFlow(tags = it.tags)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectBullets(bullets: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        bullets.forEach { b ->
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

@Composable
private fun TagFlow(tags: List<String>) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.forEach { t ->
            TinyChip(
                text = t,
                background = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
