package com.danielchi0716.resume.core

import com.danielchi0716.resume.core.model.Locale

/** Convenience for the iOS Swift code: avoids exposing receiver-style extension function. */
fun resumeService(locale: Locale): ResumeService = ResumeCore.service(locale)
