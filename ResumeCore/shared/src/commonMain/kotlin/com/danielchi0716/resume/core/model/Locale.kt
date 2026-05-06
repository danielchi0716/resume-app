package com.danielchi0716.resume.core.model

/**
 * Supported content locales. [code] is the directory key under which the
 * resume JSON files are organised on the data origin (e.g. `data/tc/meta.json`).
 */
enum class Locale(val code: String) {
    TraditionalChinese("tc"),
    English("en"),
}
