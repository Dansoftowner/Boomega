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

package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.config.ConfigAdapter
import com.google.gson.*
import javafx.scene.input.KeyCodeCombination
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

class KeyBindingsAdapter() : ConfigAdapter<KeyBindings> {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeyBindingsAdapter::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun serialize(
        src: KeyBindings?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement = src?.let {
        JsonObject().apply {
            src.javaClass.declaredFields
                .filter { it.type == KeyBinding::class.java }
                .map { it.apply { isAccessible = true }.get(src) }
                .map { it as KeyBinding }
                .forEach { keyBinding ->
                    keyBinding.takeIf { it.keyCombination != it.defaultKeyCombination }?.let {
                        logger.debug("Writing key combination for '{}': '{}'...", it.id, it.keyCombination)
                        this.addProperty(
                            it.id,
                            it.keyCombination.toString()
                        )
                    }
                }
        }
    } ?: JsonNull.INSTANCE

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ) = KeyBindings.apply {
        this.javaClass.declaredFields
            .filter { it.type == KeyBinding::class.java }
            .forEach { field ->
                field.apply { isAccessible = true }
                    .get(this)
                    .let { it as KeyBinding }
                    .let { keyBinding ->
                        try {
                            keyBinding.keyCombinationProperty.set(
                                json?.asJsonObject?.get(field.name)?.asString?.let {
                                    logger.debug("Parsing key combination for '${keyBinding.id}': '$it'...")
                                    KeyCodeCombination.valueOf(it)
                                } ?: keyBinding.defaultKeyCombination
                            )
                        } catch (e: RuntimeException) {
                            logger.error("Couldn't parse key binding", e)
                        }
                    }
            }
    }
}