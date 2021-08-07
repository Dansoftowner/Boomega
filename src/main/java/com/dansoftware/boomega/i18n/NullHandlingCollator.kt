/*
 * Boomega
 * Copyright (C)  $originalComment.match("Copyright (\d+)", 1, "-")2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.i18n

import java.text.CollationKey
import java.text.Collator

/**
 * A wrapper for [Collator] objects for handling null values when comparing strings.
 */
class NullHandlingCollator(private val baseCollator: Collator) : Collator() {
    override fun hashCode(): Int = baseCollator.hashCode()
    override fun compare(source: String?, target: String?): Int = baseCollator.compare(source ?: "", target ?: "")
    override fun getCollationKey(source: String?): CollationKey = baseCollator.getCollationKey(source)
}