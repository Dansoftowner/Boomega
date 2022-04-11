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

package com.dansoftware.boomega.database.api

abstract class DatabaseMeta() {

    @Suppress("UNCHECKED_CAST")
    abstract val provider: DatabaseProvider<DatabaseMeta>

    /**
     * The simple name of the database
     */
    abstract val name: String

    /**
     * The uri of the database
     */
    abstract val uri: String

    /**
     * The unique identifier (usually the same as the [uri]) of the database
     */
    open val identifier: String get() = uri

    /**
     * The [Action]s supported by the database-meta
     */
    protected abstract val supportedActions: Set<Action<*>>

    operator fun <T> get(action: Action<T>): T = performAction(action)

    abstract fun <T> performAction(action: Action<T>): T

    open fun isActionSupported(action: Action<*>): Boolean {
        return action in supportedActions
    }

    override fun toString(): String {
        return identifier
    }

    sealed class Action<T> {
        object Exists : Action<Boolean>()
        object SizeInBytes : Action<Long>()
        object OpenInExternalApplication: Action<Unit>()
    }
}