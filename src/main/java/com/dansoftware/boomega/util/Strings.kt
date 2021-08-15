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

@Suppress("NOTHING_TO_INLINE")
inline fun String.surrounding(prefixSuffix: String) = this.surrounding(prefixSuffix, prefixSuffix)

@Suppress("NOTHING_TO_INLINE")
inline fun String.surrounding(prefix: String, suffix: String) = "$prefix$this$suffix"

inline fun String?.ifNotBlank(block: (String) -> Unit) {
    this?.takeIf(String::isNotBlank)?.let { block(it) }
}