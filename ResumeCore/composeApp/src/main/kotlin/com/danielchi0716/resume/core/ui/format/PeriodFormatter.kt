package com.danielchi0716.resume.core.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Period
import com.danielchi0716.resume.core.model.YearMonth

private val EnMonthShort = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)

@Composable
fun isChineseLocale(): Boolean {
    val cfg = LocalConfiguration.current
    return cfg.locales[0].language.equals("zh", ignoreCase = true)
}

@Composable
fun formatPeriod(period: Period): String {
    val useChinese = isChineseLocale()
    val present = stringResource(R.string.period_present)
    return formatPeriodInternal(period, useChinese, present)
}

@Composable
fun formatDuration(period: Period): String {
    val end = period.end ?: nowYearMonth()
    val months = monthsBetween(period.start, end).coerceAtLeast(0)
    val y = months / 12
    val m = months % 12
    return when {
        y == 0 -> stringResource(R.string.duration_months, m)
        m == 0 -> stringResource(R.string.duration_years, y)
        else -> stringResource(R.string.duration_years_months, y, m)
    }
}

fun yearRangeShort(period: Period): String {
    val end = period.end
    return if (end == null || period.start.year == end.year) {
        "${period.start.year}"
    } else {
        "${period.start.year}–${end.year}"
    }
}

private fun formatPeriodInternal(
    period: Period,
    useChinese: Boolean,
    presentLabel: String,
): String {
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
    val endLabel = end?.let { formatYearMonth(it, useChinese) } ?: presentLabel
    return "${formatYearMonth(start, useChinese)} ─ $endLabel"
}

private fun formatYearMonth(ym: YearMonth, useChinese: Boolean): String =
    if (useChinese) "${ym.year}/${ym.month.toString().padStart(2, '0')}"
    else "${EnMonthShort[ym.month - 1]} ${ym.year}"

private fun monthsBetween(start: YearMonth, end: YearMonth): Int =
    (end.year - start.year) * 12 + (end.month - start.month)

internal fun nowYearMonth(): YearMonth {
    val cal = java.util.Calendar.getInstance()
    return YearMonth(
        cal.get(java.util.Calendar.YEAR),
        cal.get(java.util.Calendar.MONTH) + 1,
    )
}

internal fun totalMonthsAtNow(period: Period): Int {
    val end = period.end ?: nowYearMonth()
    return monthsBetween(period.start, end).coerceAtLeast(0)
}
