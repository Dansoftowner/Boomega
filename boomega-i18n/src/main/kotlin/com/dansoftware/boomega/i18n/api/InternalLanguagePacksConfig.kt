/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.i18n.api

import com.dansoftware.boomega.util.ReflectionUtils
import com.dansoftware.boomega.util.resJson
import com.dansoftware.boomega.util.toImmutableList
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.inject.ImplementedBy
import com.google.inject.Singleton
import org.jetbrains.annotations.TestOnly

/**
 * Entity representing the default configuration for internal language packs.
 * It defines what language-packs should be included by default, what's the fallback language pack
 * and so on.
 */
@ImplementedBy(DefaultLanguagePacksConfig::class)
internal open class InternalLanguagePacksConfig @TestOnly constructor(json: JsonElement) {

    /**
     * The class-name of the default internal language pack
     */
    lateinit var fallbackLanguagePackClassName: String private set

    /**
     * The list of class-names of the internal language packs
     */
    lateinit var languagePackClassNames: List<String> private set

    /**
     * Used as a cache for preventing multiple instances to be created from the same language-pack classes
     */
    private val instanceCache = mutableMapOf<String, LanguagePack>()

    /**
     * The default language-pack instantiated
     */
    val fallbackLanguagePack: LanguagePack by lazy {
        construct(fallbackLanguagePackClassName).also(::cacheInstance)
    }

    /**
     * The list of the internal instantiated language-packs
     */
    val languagePacks: List<LanguagePack> by lazy {
        languagePackClassNames.map {
            instanceCache[it] ?: construct(it).also(::cacheInstance)
        }
    }

    /**
     * The locale of the [fallbackLanguagePack]
     */
    val fallbackLocale get() = fallbackLanguagePack.locale

    init {
        parseJson(json.asJsonObject)
    }

    private fun parseJson(json: JsonObject) {
        with(json) {
            fallbackLanguagePackClassName = asJsonObject[FALLBACK_PACK_KEY].asString
            languagePackClassNames = asJsonObject[ALL_PACKS_KEY].asJsonArray
                .map { it.asString }
                .toMutableList()
                .apply { add(0, fallbackLanguagePackClassName) }
                .toImmutableList()
        }
    }

    private fun cacheInstance(instance: LanguagePack) {
        instanceCache[instance::class.qualifiedName!!] = instance
    }

    private fun construct(className: String): LanguagePack = ReflectionUtils.constructObject(parseClass(className))

    @Suppress("UNCHECKED_CAST")
    private fun parseClass(className: String) = Class.forName(className) as Class<out LanguagePack>

    companion object {
        const val FALLBACK_PACK_KEY = "fallback"
        const val ALL_PACKS_KEY = "classNames"
    }
}

/**
 * The language-pack config that reads from the default configuration file.
 */
@Singleton
private class DefaultLanguagePacksConfig : InternalLanguagePacksConfig(resJson(INTERNAL_LANGUAGE_PACKS, this::class)) {
    companion object {
        /**
         * The path of the resource containing the internal language pack names
         */
        private const val INTERNAL_LANGUAGE_PACKS = "internal_lang_packs.json"
    }
}