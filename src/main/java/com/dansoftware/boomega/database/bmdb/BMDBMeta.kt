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

package com.dansoftware.boomega.database.bmdb

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.util.revealInExplorer
import com.dansoftware.boomega.util.shortenedPath
import java.io.File

class BMDBMeta(override val name: String, val file: File) : DatabaseMeta() {

    @Suppress("UNCHECKED_CAST")
    override val provider: DatabaseProvider<DatabaseMeta>
        get() = BMDBProvider as DatabaseProvider<DatabaseMeta>

    override val uri: String
        get() = file.absolutePath

    override val supportedActions: Set<Action<*>>
        get() = setOf(Action.OpenInExternalApplication, Action.Exists)

    private val stringFormat by lazy {
        String.format("%s (%s)", this.name, file.shortenedPath(maxBack = 1))
    }

    constructor(file: File) : this(file.nameWithoutExtension, file)

    override fun toString(): String {
        return stringFormat
    }

    override fun isActionSupported(action: Action<*>): Boolean {
        return when (action) {
            Action.SizeInBytes -> this[Action.Exists]
            else -> super.isActionSupported(action)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> performAction(action: Action<T>): T {
        return when (action) {
            Action.SizeInBytes -> (if (this[Action.Exists]) file.length() else -1) as T
            Action.OpenInExternalApplication -> file.revealInExplorer() as T
            Action.Exists -> file.exists().and(!file.isDirectory) as T
        }
    }

    override fun equals(other: Any?): Boolean {
        return file == (other as? BMDBMeta)?.file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }
}