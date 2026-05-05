@file:OptIn(ExperimentalTime::class)

package com.danielchi0716.resume.core.model

import kotlin.jvm.JvmInline
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@JvmInline
value class Url(val raw: String)

enum class ContactType {
    EMAIL,
    PHONE,
    GITHUB,
    LINKEDIN,
    UNKNOWN,
}

data class YearMonth(val year: Int, val month: Int) : Comparable<YearMonth> {
    init {
        require(month in 1..12) { "month must be 1..12, got $month" }
    }

    override fun compareTo(other: YearMonth): Int =
        compareValuesBy(this, other, YearMonth::year, YearMonth::month)

    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}"

    companion object {
        fun parse(input: String): YearMonth {
            val parts = input.split('-')
            require(parts.size == 2) { "Expected 'yyyy-MM', got '$input'" }
            return YearMonth(parts[0].toInt(), parts[1].toInt())
        }
    }
}

data class Period(
    val start: YearMonth,
    val end: YearMonth? = null,
)

data class Tagline(
    val text: String,
    val keywords: List<String>,
)

data class Photo(
    val url: Url,
    val alt: String,
)

data class Contact(
    val type: ContactType,
    val value: String,
    val url: Url,
)

data class Header(
    val name: String,
    val subtitle: String,
    val tagline: Tagline,
    val photo: Photo,
    val contacts: List<Contact>,
)

data class Labels(
    val pageTitle: String,
)

data class Meta(
    val version: Int,
    val lang: String,
    val updatedAt: Instant,
    val labels: Labels,
)

data class YearItem(
    val years: List<Int>,
    val title: String,
    val description: String? = null,
    val bullets: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

sealed class Project {
    abstract val id: String
    abstract val name: String
    abstract val period: Period
    abstract val summary: String?

    data class YearList(
        override val id: String,
        override val name: String,
        override val period: Period,
        override val summary: String?,
        val yearItems: List<YearItem>,
    ) : Project()

    data class Bullets(
        override val id: String,
        override val name: String,
        override val period: Period,
        override val summary: String?,
        val bullets: List<String>,
    ) : Project()
}

data class WorkExperience(
    val id: String,
    val company: String,
    val title: String,
    val period: Period,
    val bullets: List<String>,
    val projects: List<Project>,
)

data class SideProject(
    val id: String,
    val name: String,
    val subtitle: String,
    val period: Period,
    val bullets: List<String>,
)

data class SkillSubcategory(
    val label: String,
    val items: List<String>,
)

sealed class Skill {
    abstract val name: String

    data class Platform(
        override val name: String,
        val subcategories: List<SkillSubcategory>,
    ) : Skill()

    data class Category(
        override val name: String,
        val tags: List<String>,
    ) : Skill()
}

data class Education(
    val id: String,
    val school: String,
    val major: String,
    val period: Period,
)

data class Language(
    val name: String,
    val level: String,
    val badge: String? = null,
)
