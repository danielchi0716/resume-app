package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.model.Locale
import org.koin.core.component.get

/** Convenience for the iOS Swift code: avoids exposing receiver-style extension function. */
fun resumeService(locale: Locale): ResumeService = ResumeCore.service(locale)

/**
 * Resolves a raw resource URL (possibly relative like "/uploads/foo.jpg") to a fully-qualified
 * https URL using the configured [NetworkConfig.host]. Mirrors [ResumeCore.resolveUrl] but
 * accepts a plain [String] so Swift callers don't need to deal with the [model.Url] value class
 * (which is erased at the iOS framework boundary).
 */
fun resolveResourceUrl(rawUrl: String): String {
    if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) return rawUrl
    val host = ResumeCore.get<NetworkConfig>().host
    return "https://$host/${rawUrl.trimStart('/')}"
}
