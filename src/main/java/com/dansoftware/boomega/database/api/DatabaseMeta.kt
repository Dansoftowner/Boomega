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

abstract class DatabaseMeta(provider: DatabaseProvider<*>) {

    @Suppress("UNCHECKED_CAST")
    val provider: DatabaseProvider<DatabaseMeta> = provider as DatabaseProvider<DatabaseMeta>

    abstract val url: String
    abstract val simpleName: String
    protected abstract val supportedActions: Set<Action<*>>

    override fun toString(): String {
        return url
    }

    operator fun <T> get(action: Action<T>): T = performAction(action)

    abstract fun <T> performAction(action: Action<T>): T

    open fun isActionSupported(action: Action<*>): Boolean {
        return action in supportedActions
    }

    sealed class Action<T> {
        object Exists : Action<Boolean>()
        object SizeInBytes : Action<Long>()
        object OpenInExternalApplication: Action<Unit>()
    }
}