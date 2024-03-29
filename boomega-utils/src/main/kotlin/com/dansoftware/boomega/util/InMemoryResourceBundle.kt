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

package com.dansoftware.boomega.util

import java.util.*

class InMemoryResourceBundle(private val map: Map<String, String>) : ResourceBundle() {

    override fun handleGetObject(key: String) = map[key]

    override fun getKeys(): Enumeration<String> = Collections.enumeration(map.keys)

    class Builder {
        private val map: MutableMap<String, String> = HashMap()

        fun put(key: String, value: String) = apply { map[key] = value }

        fun build() = InMemoryResourceBundle(map)
    }
}