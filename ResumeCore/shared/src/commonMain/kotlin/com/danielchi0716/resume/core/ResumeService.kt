package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.WorkExperience
import org.koin.core.component.get

fun ResumeCore.service(): ResumeService = get()
interface ResumeService {
    suspend fun getMeta(locale: String): Meta
    suspend fun getHeader(locale: String): Header
    suspend fun getWorkExperience(locale: String): List<WorkExperience>
    suspend fun getSideProjects(locale: String): List<SideProject>
    suspend fun getSkills(locale: String): List<Skill>
    suspend fun getEducation(locale: String): List<Education>
    suspend fun getLanguages(locale: String): List<Language>
    suspend fun getAbout(locale: String): List<String>
}
