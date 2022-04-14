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

@file:JvmName("Arguments")

package com.dansoftware.boomega.main

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.bmdb.BMDBMeta
import java.io.File

/**
 * Parses the application arguments and gives the database-representation object ([DatabaseMeta])
 */
fun parseArguments(args: List<String>?) = parseArgument(args?.getOrNull(0))

/**
 * Parses a single application argument into a [DatabaseMeta]
 */
fun parseArgument(arg: String?): DatabaseMeta? {
    return arg?.takeIf { it.isNotBlank() }?.let {
        val file = File(it)
        BMDBMeta(file)
    }
}