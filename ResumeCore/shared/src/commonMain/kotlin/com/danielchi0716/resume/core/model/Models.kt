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
    val englishName: String,
    val subtitle: String,
    val tagline: Tagline,
    val photo: Photo,
    val contacts: List<Contact>,
) {
    val monogram: String
        get() = englishName.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
}

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

enum class SkillId(val raw: String) {
    AI_LLM("ai-llm"),
    ARCHITECTURE("architecture"),
    KMP("kmp"),
    ANDROID("android"),
    IOS("ios"),
    TOOLS("tools");

    companion object {
        fun from(raw: String): SkillId =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown skill id: $raw")
    }
}

sealed class Skill {
    abstract val id: SkillId

    data class Platform(
        override val id: SkillId,
        val subcategories: List<SkillSubcategory>,
    ) : Skill()

    data class Category(
        override val id: SkillId,
        val tags: List<String>,
    ) : Skill()
}

data class Education(
    val id: String,
    val school: String,
    val major: String,
    val period: Period,
)

enum class LanguageCode(val raw: String) {
    ZH("zh"),
    EN("en");

    companion object {
        fun from(raw: String): LanguageCode =
            entries.firstOrNull { it.raw == raw }
                ?: error("Unknown language code: $raw")
    }
}

enum class LanguageSkill {
    BASIC,
    INTERMEDIATE,
    ADVANCED,
    FLUENT;

    companion object {
        fun from(raw: String): LanguageSkill = when (raw) {
            "basic"        -> BASIC
            "intermediate" -> INTERMEDIATE
            "advanced"     -> ADVANCED
            "fluent"       -> FLUENT
            else           -> error("Unknown language skill: $raw")
        }
    }
}

sealed class LanguageLevel {
    data object Native : LanguageLevel()
    data class Proficiency(
        val listening: LanguageSkill,
        val speaking: LanguageSkill,
        val reading: LanguageSkill,
        val writing: LanguageSkill,
    ) : LanguageLevel()
}

data class Language(
    val code: LanguageCode,
    val level: LanguageLevel,
    val badge: String? = null,
)
