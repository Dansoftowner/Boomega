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

package com.dansoftware.boomega.database

import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.database.bmdb.BMDBProvider
import com.dansoftware.boomega.database.mysql.MySQLProvider
import com.dansoftware.boomega.util.toImmutableList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private val logger: Logger = LoggerFactory.getLogger("SupportedExporters")

object SupportedDatabases : List<DatabaseProvider<*>> by LinkedList(loadProviders()).toImmutableList()

private fun loadProviders() =
    loadBuiltInProviders().plus(loadProvidersFromPlugins())
        .onEach { logger.debug("Found database provider '{}'", it.javaClass.name) }
        .toList()

private fun loadBuiltInProviders() = sequenceOf<DatabaseProvider<*>>(
    BMDBProvider,
    MySQLProvider
)

private fun loadProvidersFromPlugins() = sequenceOf<DatabaseProvider<*>>() // TODO: load from plugins
