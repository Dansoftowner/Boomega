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
package com.dansoftware.boomega.db

import com.jfilegoodies.FileGoodies
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

/**
 * A DatabaseMeta can hold all meta-information about a particular database.
 */
open class DatabaseMeta @JvmOverloads constructor(var name: String, var file: File? = null) {
    private var stringFormat: String? = null

    constructor(file: File) : this(FilenameUtils.getBaseName(file.name), file)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val databaseMeta = other as DatabaseMeta
        return file == databaseMeta.file
    }

    override fun hashCode(): Int {
        return Objects.hash(file?.toString() ?: name)
    }

    override fun toString(): String =
        stringFormat ?: String.format("%s (%s)", name, file?.let { FileGoodies.shortenedFilePath(it, 1) } ?: "null")
            .also { stringFormat = it }
}