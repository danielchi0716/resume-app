package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.model.Period
import com.danielchi0716.resume.core.model.YearMonth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        TestScreen()
    }
}

private sealed interface LoadState {
    data object Idle : LoadState
    data object Loading : LoadState
    data class Success(val text: String) : LoadState
    data class Error(val message: String) : LoadState
}

@Composable
private fun TestScreen() {
    var locale by remember { mutableStateOf("en") }
    var state by remember { mutableStateOf<LoadState>(LoadState.Idle) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize().safeContentPadding(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LocaleButton("EN", selected = locale == "en") { locale = "en" }
                LocaleButton("TC", selected = locale == "tc") { locale = "tc" }
            }

            Button(
                onClick = {
                    scope.launch {
                        state = LoadState.Loading
                        state = runCatching { fetchSummary(locale) }
                            .fold(
                                onSuccess = { LoadState.Success(it) },
                                onFailure = { LoadState.Error(it.toString()) },
                            )
                    }
                },
                enabled = state !is LoadState.Loading,
            ) {
                Text("Load ($locale)")
            }

            when (val s = state) {
                LoadState.Idle -> Text("Press Load.")
                LoadState.Loading -> CircularProgressIndicator()
                is LoadState.Success -> Text(
                    text = s.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
                is LoadState.Error -> Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
            }
        }
    }
}

@Composable
private fun LocaleButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}

private val EN_MONTH_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

private fun formatYearMonth(ym: YearMonth, locale: String): String =
    if (locale == "tc") "${ym.year}/${ym.month.toString().padStart(2, '0')}"
    else "${EN_MONTH_SHORT[ym.month - 1]} ${ym.year}"

private fun formatPeriod(period: Period, locale: String): String {
    val start = period.start
    val end = period.end
    val startsInJan = start.month == 1
    val endsInDec = end != null && end.month == 12
    if (startsInJan && (endsInDec || end == null)) {
        return when {
            end == null || start.year == end.year -> "${start.year}"
            else -> "${start.year} ─ ${end.year}"
        }
    }
    val present = if (locale == "tc") "至今" else "Present"
    val endLabel = end?.let { formatYearMonth(it, locale) } ?: present
    return "${formatYearMonth(start, locale)} ─ $endLabel"
}

private suspend fun fetchSummary(locale: String): String {
    val s = ResumeCore.service()
    val sb = StringBuilder()
    val meta = s.getMeta(locale)
    sb.appendLine("=== Meta ===")
    sb.appendLine("lang=${meta.lang} version=${meta.version} updatedAt=${meta.updatedAt}")
    sb.appendLine("title=${meta.labels.pageTitle}")
    sb.appendLine()

    val header = s.getHeader(locale)
    sb.appendLine("=== Header ===")
    sb.appendLine("name=${header.name}")
    sb.appendLine("subtitle=${header.subtitle}")
    sb.appendLine("contacts=${header.contacts.size}")
    sb.appendLine()

    val work = s.getWorkExperience(locale)
    sb.appendLine("=== WorkExperience (${work.size}) ===")
    work.forEach { sb.appendLine("- ${it.company} (${formatPeriod(it.period, locale)}), projects=${it.projects.size}") }
    sb.appendLine()

    val side = s.getSideProjects(locale)
    sb.appendLine("=== SideProjects (${side.size}) ===")
    side.forEach { sb.appendLine("- ${it.name}") }
    sb.appendLine()

    val skills = s.getSkills(locale)
    sb.appendLine("=== Skills (${skills.size}) ===")
    skills.forEach {
        val label = when (it) {
            is com.danielchi0716.resume.core.model.Skill.Platform -> "platform/${it.subcategories.size} subs"
            is com.danielchi0716.resume.core.model.Skill.Category -> "category/${it.tags.size} tags"
        }
        sb.appendLine("- ${it.name} ($label)")
    }
    sb.appendLine()

    val edu = s.getEducation(locale)
    sb.appendLine("=== Education (${edu.size}) ===")
    edu.forEach { sb.appendLine("- ${it.school} — ${it.major}") }
    sb.appendLine()

    val langs = s.getLanguages(locale)
    sb.appendLine("=== Languages (${langs.size}) ===")
    langs.forEach { sb.appendLine("- ${it.name}: ${it.level}") }
    sb.appendLine()

    val about = s.getAbout(locale)
    sb.appendLine("=== About (${about.size} paragraphs) ===")
    about.forEachIndexed { i, p -> sb.appendLine("[${i + 1}] ${p.take(80)}...") }

    return sb.toString()
}
