/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

@file:Suppress("NOTHING_TO_INLINE")

package com.dansoftware.boomega.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Removes the property from this [JsonObject].
 *
 * @param key name of the member that should be removed.
 * @return the [JsonElement] object that is being removed.
 */
inline operator fun JsonObject.minus(key: String): JsonElement? = remove(key)

/**
 * @see JsonObject.addProperty
 */
inline operator fun JsonObject.set(key: String, value: String) = addProperty(key, value)

/**
 * @see JsonObject.addProperty
 */
inline operator fun JsonObject.set(key: String, value: Number) = addProperty(key, value)

/**
 * @see JsonObject.addProperty
 */
inline operator fun JsonObject.set(key: String, value: Char) = addProperty(key, value)

/**
 * @see JsonObject.addProperty
 */
inline operator fun JsonObject.set(key: String, value: Boolean) = addProperty(key, value)