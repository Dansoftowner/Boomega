package com.dansoftware.boomega.i18n

import com.dansoftware.boomega.util.Person
import java.util.*

/**
 * An [InternalLanguagePack] is an abstract [LanguagePack] implementation
 * that represents a Language pack that is nested into the application by default.
 *
 * @author Daniel Gyorffy
 */
private abstract class InternalLanguagePack(locale: Locale) : LanguagePack(locale) {

    companion object {
        private const val VALUES = "com.dansoftware.boomega.i18n.Values"
    }

    override fun getTranslator(): Person? = null

    override fun getValues(): ResourceBundle = getBundle(VALUES)
}