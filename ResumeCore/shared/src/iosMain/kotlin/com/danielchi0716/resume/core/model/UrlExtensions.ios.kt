package com.danielchi0716.resume.core.model

import platform.Foundation.NSURL

fun Url.toNSURL(): NSURL? = NSURL.URLWithString(raw)
