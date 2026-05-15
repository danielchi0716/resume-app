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
import kotlin.coroutines.cancellation.CancellationException
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * A locale-bound resume reader. Acquire via [ResumeCore.service].
 *
 * Suspend functions declare [Throwable] in [Throws] so that Kotlin/Native exposes
 * them as `throws` in Swift; without this, Ktor errors (e.g. IO failures when the
 * device is offline) bypass Swift's `catch` and abort the process. [CancellationException]
 * is also declared so Swift `Task` cancellation propagates without terminating.
 */
interface ResumeService {
    @Throws(CancellationException::class, Throwable::class)
    suspend fun getMeta(): Meta

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getHeader(): Header

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getWorkExperience(): List<WorkExperience>

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getSideProjects(): List<SideProject>

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getSkills(): List<Skill>

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getEducation(): List<Education>

    @Throws(CancellationException::class, Throwable::class)
    suspend fun getLanguages(): List<Language>

    @Throws(CancellationException::class, Throwable::class)
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
