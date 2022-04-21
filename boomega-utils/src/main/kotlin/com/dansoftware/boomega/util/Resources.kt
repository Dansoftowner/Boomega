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

@file:JvmName("Resources")

package com.dansoftware.boomega.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.InputStream
import java.net.URL
import kotlin.reflect.KClass

/**
 * Gives the resource as a [URL].
 */
@JvmOverloads
fun res(path: String, clazz: Class<*> = R::class.java): URL? = clazz.getResource(path)

/**
 * Gives the resource as a [URL].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun res(path: String, clazz: KClass<*> = R::class): URL? = res(path, clazz.java)

/**
 * Gives the resource as an [java.io.InputStream].
 */
@JvmOverloads
fun resStream(path: String, clazz: Class<*> = R::class.java): InputStream? = clazz.getResourceAsStream(path)

/**
 * Gives the resource as an [java.io.InputStream].
 */
@Suppress("NOTHING_TO_INLINE")
inline fun resStream(path: String, clazz: KClass<*> = R::class): InputStream? = resStream(path, clazz.java)

/**
 * Loads the resource into a [JsonElement]. Asserts that the resource has the json format.
 */
@JvmOverloads
fun resJson(path: String, clazz: Class<*> = R::class.java): JsonElement =
    resStream(path, clazz)!!.use { Gson().fromJson(it.bufferedReader(), JsonElement::class.java) }

/**
 * Loads the resource into a [JsonElement]. Asserts that the resource has the json format.
 */
fun resJson(path: String, clazz: KClass<*> = R::class) = resJson(path, clazz.java)

/**
 * Utility class used for loading resources
 */
private class R