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

import com.jfilegoodies.explorer.FileExplorers
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

fun File.revealInExplorer() {
    FileExplorers.get().openSelect(this)
}

fun <I, T : Collection<I>> T.ifNotEmpty(block: (T) -> Unit): T {
    if (this.isNotEmpty())
        block(this)
    return this
}

fun String.surrounding(prefixSuffix: String) = this.surrounding(prefixSuffix, prefixSuffix)

fun String.surrounding(prefix: String, suffix: String) = (prefix + this + suffix)

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun String.equalsIgnoreCase(other: String): Boolean =
    (this as java.lang.String).equalsIgnoreCase(other)

fun isServerReachable(url: String) =
    try {
        URL("http://www.google.com").openConnection().connect()
        true
    } catch (e: MalformedURLException) {
        false
    } catch (e: IOException) {
        false
    }