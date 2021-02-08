package com.dansoftware.libraryapp.i18n

import java.util.*

/**
 * An [InternalLanguagePack] is an abstract [LanguagePack] implementation
 * that represents a Language pack that is nested into the application by default.
 *
 * @author Daniel Gyorffy
 */
abstract class InternalLanguagePack(locale: Locale) : LanguagePack(locale) {

    companion object {
        private const val VALUES = "com.dansoftware.libraryapp.i18n.Values"
    }

    override fun getTranslator(): LanguageTranslator? {
        return LanguageTranslator("Dániel", "Györffy", "dansoftwareowner@gmail.com")
    }

    override fun getValues(): ResourceBundle = getBundle(VALUES)
}