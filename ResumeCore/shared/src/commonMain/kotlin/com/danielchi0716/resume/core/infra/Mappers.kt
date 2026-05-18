@file:OptIn(ExperimentalTime::class)

package com.danielchi0716.resume.core.infra

import com.danielchi0716.resume.core.model.Contact
import com.danielchi0716.resume.core.model.ContactType
import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Labels
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.Period
import com.danielchi0716.resume.core.model.Photo
import com.danielchi0716.resume.core.model.Project
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.SkillSubcategory
import com.danielchi0716.resume.core.model.Tagline
import com.danielchi0716.resume.core.model.Url
import com.danielchi0716.resume.core.model.WorkExperience
import com.danielchi0716.resume.core.model.YearItem
import com.danielchi0716.resume.core.model.YearMonth
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal fun PeriodBean.toModel() = Period(
    start = YearMonth.parse(start),
    end = end?.let(YearMonth::parse),
)

internal fun TaglineBean.toModel() = Tagline(text, keywords)

internal fun PhotoBean.toModel() = Photo(Url(url), alt)

internal fun ContactBean.toModel() = Contact(
    type = type.toContactType(),
    value = value,
    url = Url(url),
)

private fun String.toContactType(): ContactType = when (this) {
    "email" -> ContactType.EMAIL
    "phone" -> ContactType.PHONE
    "github" -> ContactType.GITHUB
    "linkedin" -> ContactType.LINKEDIN
    else -> ContactType.UNKNOWN
}

internal fun HeaderBean.toModel() = Header(
    name = name,
    englishName = englishName,
    subtitle = subtitle,
    tagline = tagline.toModel(),
    photo = photo.toModel(),
    contacts = contacts.map { it.toModel() },
)

internal fun LabelsBean.toModel() = Labels(
    pageTitle = pageTitle,
)

internal fun MetaBean.toModel() = Meta(
    version = version,
    lang = lang,
    updatedAt = Instant.parse(updatedAt),
    labels = labels.toModel(),
)

internal fun YearItemBean.toModel() = YearItem(
    years = years.map { it.toInt() },
    title = title,
    description = description,
    bullets = bullets,
    tags = tags,
)

internal fun ProjectBean.toModel(): Project = when (type) {
    "yearList" -> Project.YearList(
        id = id,
        name = name,
        period = period.toModel(),
        summary = summary,
        yearItems = yearItems.map { it.toModel() },
    )
    "bullets" -> Project.Bullets(
        id = id,
        name = name,
        period = period.toModel(),
        summary = summary,
        bullets = bullets,
    )
    else -> error("Unknown project type: $type")
}

internal fun WorkExperienceBean.toModel() = WorkExperience(
    id = id,
    company = company,
    title = title,
    period = period.toModel(),
    bullets = bullets,
    projects = projects.map { it.toModel() },
)

internal fun SideProjectBean.toModel() = SideProject(
    id = id,
    name = name,
    subtitle = subtitle,
    period = period.toModel(),
    bullets = bullets,
)

internal fun SkillSubcategoryBean.toModel() = SkillSubcategory(label, items)

internal fun SkillBean.toModel(): Skill = when (type) {
    "platform" -> Skill.Platform(
        name = name,
        subcategories = subcategories.map { it.toModel() },
    )
    "category" -> Skill.Category(
        name = name,
        tags = tags,
    )
    else -> error("Unknown skill type: $type")
}

internal fun EducationBean.toModel() = Education(
    id = id,
    school = school,
    major = major,
    period = period.toModel(),
)

internal fun LanguageBean.toModel() = Language(name, level, badge)
