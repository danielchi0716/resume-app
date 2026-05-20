@file:OptIn(ExperimentalTime::class)

package com.danielchi0716.resume.core.ui.utils

import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.danielchi0716.resume.core.R
import com.danielchi0716.resume.core.model.Period
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.YearMonthRange
import kotlinx.datetime.number
import kotlinx.datetime.toJavaYearMonth
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun currentYearMonth(): YearMonth {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return YearMonth(today.year, today.month)
}

/** Inclusive `start..end` range; an open-ended period resolves [Period.end] to [now]. */
fun Period.toYearMonthRange(now: YearMonth = currentYearMonth()): YearMonthRange =
    start..(end ?: now)

/** Locale-native abbreviated month + year, e.g. "Mar 2020" (en) or "2020年3月" (zh-Hant). */
fun YearMonth.formatted(locale: Locale): String {
    val pattern = DateFormat.getBestDateTimePattern(locale, "yMMM")
    return toJavaYearMonth().format(DateTimeFormatter.ofPattern(pattern, locale))
}

/** "Mar 2020 ─ Jul 2022" or, for an open-ended period, "Mar 2020 ─ Present". */
@Composable
fun Period.formatted(locale: Locale): String {
    val present = stringResource(R.string.period_present)
    return "${start.formatted(locale)} ─ ${end?.formatted(locale) ?: present}"
}

/** Locale-native condensed duration, e.g. "3 yr, 2 mo" (en) or "3 年 2 個月" (zh-Hant). */
fun YearMonthRange.formatted(locale: Locale): String {
    val totalMonths = (endInclusive.year - start.year) * 12 +
        (endInclusive.month.number - start.month.number)
    val ys = totalMonths / 12
    val ms = totalMonths % 12
    val measures = when {
        ys == 0 -> arrayOf(Measure(ms, MeasureUnit.MONTH))
        ms == 0 -> arrayOf(Measure(ys, MeasureUnit.YEAR))
        else -> arrayOf(Measure(ys, MeasureUnit.YEAR), Measure(ms, MeasureUnit.MONTH))
    }
    return MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT)
        .formatMeasures(*measures)
}
