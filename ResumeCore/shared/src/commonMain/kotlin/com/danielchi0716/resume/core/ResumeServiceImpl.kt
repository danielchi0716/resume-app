package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.infra.ResumeInfraService
import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.WorkExperience

internal class ResumeServiceImpl(
    private val infra: ResumeInfraService,
) : ResumeService {

    override suspend fun getMeta(locale: String): Meta = infra.getMeta(locale)

    override suspend fun getHeader(locale: String): Header = infra.getHeader(locale)

    override suspend fun getWorkExperience(locale: String): List<WorkExperience> =
        infra.getWorkExperience(locale)

    override suspend fun getSideProjects(locale: String): List<SideProject> =
        infra.getSideProjects(locale)

    override suspend fun getSkills(locale: String): List<Skill> = infra.getSkills(locale)

    override suspend fun getEducation(locale: String): List<Education> = infra.getEducation(locale)

    override suspend fun getLanguages(locale: String): List<Language> = infra.getLanguages(locale)

    override suspend fun getAbout(locale: String): List<String> = infra.getAbout(locale)
}
