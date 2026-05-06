package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.model.Education
import com.danielchi0716.resume.core.model.Header
import com.danielchi0716.resume.core.model.Language
import com.danielchi0716.resume.core.model.Locale
import com.danielchi0716.resume.core.model.Meta
import com.danielchi0716.resume.core.model.SideProject
import com.danielchi0716.resume.core.model.Skill
import com.danielchi0716.resume.core.model.Url
import com.danielchi0716.resume.core.model.WorkExperience
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * A locale-bound resume reader. Acquire via [ResumeCore.service].
 */
interface ResumeService {
    suspend fun getMeta(): Meta
    suspend fun getHeader(): Header
    suspend fun getWorkExperience(): List<WorkExperience>
    suspend fun getSideProjects(): List<SideProject>
    suspend fun getSkills(): List<Skill>
    suspend fun getEducation(): List<Education>
    suspend fun getLanguages(): List<Language>
    suspend fun getAbout(): List<String>
}

fun ResumeCore.service(locale: Locale): ResumeService =
    get { parametersOf(locale) }

fun ResumeCore.resolveUrl(url: Url): String {
    val raw = url.raw
    if (raw.startsWith("http://") || raw.startsWith( "https://")) return raw
    val host = get<NetworkConfig>().host
    return "https://$host/${raw.trimStart('/')}"
}
