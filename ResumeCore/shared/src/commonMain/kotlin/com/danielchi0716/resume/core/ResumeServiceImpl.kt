package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.infra.ResumeInfraService
import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.WorkExperience

internal class ResumeServiceImpl(
    private val infra: ResumeInfraService,
    private val locale: Locale,
) : ResumeService {

    private val code: String get() = locale.code

    override suspend fun getMeta(): Meta = infra.getMeta(code)

    override suspend fun getHeader(): Header = infra.getHeader(code)

    override suspend fun getWorkExperience(): List<WorkExperience> =
        infra.getWorkExperience(code)

    override suspend fun getSideProjects(): List<SideProject> =
        infra.getSideProjects(code)

    override suspend fun getSkills(): List<Skill> = infra.getSkills(code)

    override suspend fun getEducation(): List<Education> = infra.getEducation(code)

    override suspend fun getLanguages(): List<Language> = infra.getLanguages(code)

    override suspend fun getAbout(): List<String> = infra.getAbout(code)
}
