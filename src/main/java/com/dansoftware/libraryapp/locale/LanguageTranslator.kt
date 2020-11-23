package com.dansoftware.libraryapp.locale

import java.util.*

/**
 * A [LanguageTranslator] represents a person who translated the app into a particular language
 */
data class LanguageTranslator(val firstName: String, val lastName: String, val email: String) {
    fun getDisplayName(locale: Locale): String {
        return when (locale.language) {
            "hu" -> lastName.plus(" ").plus(firstName)
            else -> firstName.plus(" ").plus(lastName)
        }
    }
}