@file:OptIn(ExperimentalMaterial3Api::class)

package com.danielchi0716.resume.core.ui.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.WorkExperience
import com.danielchi0716.resume.core.ui.common.UiState
import com.danielchi0716.resume.core.ui.common.ErrorState
import com.danielchi0716.resume.core.ui.common.LoadingState
import com.danielchi0716.resume.core.ui.common.ResumeTopAppBar
import com.danielchi0716.resume.core.ui.common.SectionLabel
import com.danielchi0716.resume.core.ui.common.TinyChip
import com.danielchi0716.resume.core.ui.common.rememberDataLocale
import com.danielchi0716.resume.core.ui.format.formatDuration
import com.danielchi0716.resume.core.ui.format.formatPeriod
import com.danielchi0716.resume.core.ui.format.nowYearMonth

private const val RouteList = "list"
private const val RouteDetail = "detail/{idx}"
private fun detailRoute(idx: Int) = "detail/$idx"

@Composable
fun WorkScreen() {
    val locale = rememberDataLocale()
    val viewModel: WorkViewModel = hiltViewModel(key = locale.code)
    val uiState by viewModel.uiState.collectAsState()

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = RouteList) {
        composable(RouteList) {
            WorkListView(
                uiState = uiState,
                onRetry = viewModel::retry,
                onOpenDetail = { idx -> navController.navigate(detailRoute(idx)) },
            )
        }
        composable(
            route = RouteDetail,
            arguments = listOf(navArgument("idx") { type = NavType.IntType }),
        ) { entry ->
            val idx = entry.arguments?.getInt("idx") ?: 0
            WorkDetailView(
                uiState = uiState,
                onRetry = viewModel::retry,
                initialIdx = idx,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun WorkListView(
    uiState: UiState<List<WorkExperience>>,
    onRetry: () -> Unit,
    onOpenDetail: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ResumeTopAppBar(
            title = stringResource(R.string.title_work),
            leadingIcon = Icons.Filled.Work,
        )
        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = uiState.message, onRetry = onRetry)
            is UiState.Ready -> WorkListContent(jobs = uiState.data, onOpenDetail = onOpenDetail)
        }
    }
}

@Composable
private fun WorkListContent(jobs: List<WorkExperience>, onOpenDetail: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        item { StatStrip(jobs = jobs) }
        item {
            SectionLabel(
                text = stringResource(R.string.section_work),
                modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 12.dp),
            )
        }
        jobs.forEachIndexed { idx, job ->
            item(key = job.id) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    JobListCard(
                        job = job,
                        idx = idx,
                        onClick = { onOpenDetail(idx) },
                    )
                }
            }
        }
    }
}

private data class WorkStat(val value: String, val label: String)

@Composable
private fun StatStrip(jobs: List<WorkExperience>) {
    val totalYears = remember(jobs) { computeTotalYears(jobs) }
    val mainProjects = remember(jobs) { jobs.sumOf { it.projects.size } }
    val stats = listOf(
        WorkStat("${totalYears}+", stringResource(R.string.stat_total_years)),
        WorkStat("${jobs.size}", stringResource(R.string.stat_companies)),
        WorkStat("${mainProjects}+", stringResource(R.string.stat_projects)),
    )
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                stats.forEach { s ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = s.value,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = s.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JobListCard(
    job: WorkExperience,
    idx: Int,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
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
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                if (job.bullets.isNotEmpty()) {
                    Text(
                        text = job.bullets.first(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.action_view_detail),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

private fun computeTotalYears(jobs: List<WorkExperience>): Int {
    if (jobs.isEmpty()) return 0
    val earliest = jobs.minOf { it.period.start.year * 12 + it.period.start.month }
    val now = nowYearMonth()
    val latest = jobs.maxOf { (it.period.end ?: now).let { e -> e.year * 12 + e.month } }
    val totalMonths = (latest - earliest).coerceAtLeast(0)
    return totalMonths / 12
}
