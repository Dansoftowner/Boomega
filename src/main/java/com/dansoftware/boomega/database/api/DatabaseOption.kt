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

package com.dansoftware.boomega.database.api

/**
 * Represents an option can be used for a particular database-system.
 */
open class DatabaseOption<T>(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: Class<T>,
    val defaultValue: T
) {
    fun getValueFrom(map: Map<DatabaseOption<*>, Any>): T {
        return map.getOrDefault(this, this.defaultValue as Any) as T
    }
}