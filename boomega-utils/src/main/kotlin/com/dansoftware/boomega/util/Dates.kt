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

@file:JvmName("Dates")

package com.dansoftware.boomega.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

inline fun LocalDate.format(format: String, ifFailed: (Exception) -> Unit = {}): String? {
    return try {
        format(DateTimeFormatter.ofPattern(format))
    } catch (e: RuntimeException) {
        ifFailed(e)
        null
    }
}

inline fun String.parseToDate(
    vararg fallbackFormats: String,
    onFailed: (format: String, DateTimeParseException) -> Unit = { _, _ -> }
): LocalDate? {
    try {
        return LocalDate.parse(this)
    } catch (e: DateTimeParseException) {
        onFailed("yyyy-MM-dd", e)
        fallbackFormats.forEach {
            try {
                return LocalDate.parse(this, DateTimeFormatter.ofPattern(it))
            } catch (e: DateTimeParseException) {
                onFailed(it, e)
            }
        }
        return null
    }
}