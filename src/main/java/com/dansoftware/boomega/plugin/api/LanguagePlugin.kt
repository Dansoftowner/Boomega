package com.dansoftware.boomega.plugin.api

import com.dansoftware.boomega.i18n.LanguagePack

/**
 * Allows to add a custom [LanguagePack] to the application.
 *
 * @author Daniel Gyoerffy
 */
interface LanguagePlugin : BoomegaPlugin {
    val languagePack: LanguagePack
}