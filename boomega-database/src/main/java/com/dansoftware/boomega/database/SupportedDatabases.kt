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

package com.dansoftware.boomega.database

import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.database.bmdb.BMDBProvider
import com.dansoftware.boomega.database.sql.mysql.MySQLProvider
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.plugin.DatabaseProviderPlugin
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.plugin.api.of
import com.dansoftware.boomega.util.toImmutableList
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SupportedExporters")

/**
 * The list of available [DatabaseProvider]s can be used by the app.
 * Includes both the internal providers and providers read from plugins.
 */
object SupportedDatabases : List<DatabaseProvider<*>> by loadProviders().toImmutableList()

private fun loadProviders() =
    loadBuiltInProviders().plus(loadProvidersFromPlugins())
        .onEach { logger.debug("Found database provider '{}'", it.javaClass.name) }
        .toList()

private fun loadBuiltInProviders() = sequenceOf<DatabaseProvider<*>>(
    BMDBProvider,
    MySQLProvider
)

private fun loadProvidersFromPlugins() =
    get(PluginService::class)
        .of(DatabaseProviderPlugin::class)
        .asSequence()
        .map(DatabaseProviderPlugin::databaseProvider)