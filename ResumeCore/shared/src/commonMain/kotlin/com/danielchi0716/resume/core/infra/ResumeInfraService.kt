package com.danielchi0716.resume.core.infra

import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.WorkExperience
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class ResumeInfraService(private val client: HttpClient) {
    suspend fun getMeta(locale: String): Meta =
        client.get("data/$locale/meta.json")
            .body<MetaBean>()
            .toModel()

    suspend fun getHeader(locale: String): Header =
        client.get("data/$locale/header.json")
            .body<HeaderBean>()
            .toModel()

    suspend fun getWorkExperience(locale: String): List<WorkExperience> =
        client.get("data/$locale/work-experience.json")
            .body<List<WorkExperienceBean>>()
            .map(WorkExperienceBean::toModel)

    suspend fun getSideProjects(locale: String): List<SideProject> =
        client.get("data/$locale/side-projects.json")
            .body<List<SideProjectBean>>()
            .map(SideProjectBean::toModel)

    suspend fun getSkills(locale: String): List<Skill> =
        client.get("data/$locale/skills.json")
            .body<List<SkillBean>>()
            .map(SkillBean::toModel)

    suspend fun getEducation(locale: String): List<Education> =
        client.get("data/$locale/education.json")
            .body<List<EducationBean>>()
            .map(EducationBean::toModel)

    suspend fun getLanguages(locale: String): List<Language> =
        client.get("data/$locale/languages.json")
            .body<List<LanguageBean>>()
            .map(LanguageBean::toModel)

    suspend fun getAbout(locale: String): List<String> =
        client.get("data/$locale/about.json").body()
}
