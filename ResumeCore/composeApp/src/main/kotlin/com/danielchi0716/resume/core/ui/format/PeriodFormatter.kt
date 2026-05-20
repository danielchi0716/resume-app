package com.danielchi0716.resume.core.ui.format

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Period
import kotlinx.datetime.YearMonth
import kotlinx.datetime.number
import kotlinx.datetime.toJavaYearMonth
import kotlinx.datetime.toKotlinYearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.YearMonth as JavaYearMonth

/**
 * Locale-native abbreviated month + year, e.g. "Mar 2020" (en) or "2020年3月" (zh-Hant).
 * Pattern is resolved per locale via `DateFormat.getBestDateTimePattern`; no manual branching.
 */
fun YearMonth.formatted(locale: Locale): String {
    val pattern = DateFormat.getBestDateTimePattern(locale, "yMMM")
    return toJavaYearMonth().format(DateTimeFormatter.ofPattern(pattern, locale))
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

private fun monthsBetween(start: YearMonth, end: YearMonth): Int =
    (end.year - start.year) * 12 + (end.month.number - start.month.number)

internal fun nowYearMonth(): YearMonth = JavaYearMonth.now().toKotlinYearMonth()

internal fun totalMonthsAtNow(period: Period): Int {
    val end = period.end ?: nowYearMonth()
    return monthsBetween(period.start, end).coerceAtLeast(0)
}
