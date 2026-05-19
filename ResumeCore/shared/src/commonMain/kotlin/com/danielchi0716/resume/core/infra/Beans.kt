package com.danielchi0716.resume.core.infra

import kotlinx.serialization.Serializable

@Serializable
internal data class PeriodBean(
    val start: String,
    val end: String? = null,
)

@Serializable
internal data class TaglineBean(
    val text: String,
    val keywords: List<String>,
)

@Serializable
internal data class PhotoBean(
    val url: String,
    val alt: String,
)

@Serializable
internal data class ContactBean(
    val type: String,
    val value: String,
    val url: String,
)

@Serializable
internal data class HeaderBean(
    val name: String,
    val englishName: String,
    val subtitle: String,
    val tagline: TaglineBean,
    val photo: PhotoBean,
    val contacts: List<ContactBean>,
)

@Serializable
internal data class LabelsBean(
    val pageTitle: String,
)

@Serializable
internal data class MetaBean(
    val version: Int,
    val lang: String,
    val updatedAt: String,
    val labels: LabelsBean,
)

@Serializable
internal data class YearItemBean(
    val years: List<String>,
    val title: String,
    val description: String? = null,
    val bullets: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
internal data class ProjectBean(
    val id: String,
    val type: String,
    val name: String,
    val period: PeriodBean,
    val summary: String? = null,
    val yearItems: List<YearItemBean> = emptyList(),
    val bullets: List<String> = emptyList(),
)

@Serializable
internal data class WorkExperienceBean(
    val id: String,
    val company: String,
    val title: String,
    val period: PeriodBean,
    val bullets: List<String>,
    val projects: List<ProjectBean> = emptyList(),
)

@Serializable
internal data class SideProjectBean(
    val id: String,
    val name: String,
    val subtitle: String,
    val period: PeriodBean,
    val bullets: List<String>,
)

@Serializable
internal data class SkillSubcategoryBean(
    val label: String,
    val items: List<String>,
)

@Serializable
internal data class SkillBean(
    val id: String,
    val type: String,
    val subcategories: List<SkillSubcategoryBean> = emptyList(),
    val tags: List<String> = emptyList(),
)

@Serializable
internal data class EducationBean(
    val id: String,
    val school: String,
    val major: String,
    val period: PeriodBean,
)

@Serializable
internal data class LanguageBean(
    val code: String,
    val level: LanguageLevelBean,
    val badge: String? = null,
)

@Serializable
internal data class LanguageLevelBean(
    val kind: String,
    val listening: String? = null,
    val speaking: String? = null,
    val reading: String? = null,
    val writing: String? = null,
)
