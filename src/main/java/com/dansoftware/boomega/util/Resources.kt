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

package com.dansoftware.boomega.util

import javafx.scene.image.Image
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import kotlin.reflect.KClass

/**
 * Gives the resource as a [URL].
 */
@JvmOverloads
fun res(path: String, clazz: KClass<*> = R::class): URL? =
    clazz.java.getResource(path)

/**
 * Gives the resource as an [java.io.InputStream].
 */
@JvmOverloads
fun resStream(path: String, clazz: KClass<*> = R::class): InputStream? =
    clazz.java.getResourceAsStream(path)

/**
 * Gives the resource loaded into an [Image] object
 */
@JvmOverloads
fun resImg(path: String, clazz: KClass<*> = R::class): Image? {
    return resStream(path, clazz)
        ?.let(::BufferedInputStream)
        ?.use(::Image)
}

/**
 * Utility class used for loading resources
 */
private class R