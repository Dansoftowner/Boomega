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
package com.dansoftware.boomega.gui.theme.config

import com.dansoftware.boomega.gui.theme.Theme
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

class ThemeDeserializer : JsonDeserializer<Theme?> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): Theme? {
        return try {
            Theme.available.find { it::class.java.name == json.asString }
                ?: throw ThemeClassNotValidException("The configured theme class '${json.asString}' has not been found")
        } catch (e : RuntimeException) {
            logger.error("Couldn't deserialize theme", e)
            Theme.default
        }
    }

    private class ThemeClassNotValidException(msg: String) : RuntimeException(msg)

    companion object {
        private val logger = LoggerFactory.getLogger(ThemeDeserializer::class.java)
    }
}