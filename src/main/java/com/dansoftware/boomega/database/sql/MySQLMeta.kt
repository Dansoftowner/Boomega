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

package com.dansoftware.boomega.database.sql

import com.dansoftware.boomega.database.api.DatabaseMeta

class MySQLMeta(override val url: String) : DatabaseMeta(MySQLProvider) {

    override val simpleName: String
        get() = url

    override val supportedActions: Set<Action<*>>
        get() = setOf()

    override fun <T> performAction(action: Action<T>): T {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return url
    }
}