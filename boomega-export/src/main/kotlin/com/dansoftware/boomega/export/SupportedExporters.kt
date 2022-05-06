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

@file:OptIn(RecordExportAPI::class)

package com.dansoftware.boomega.export

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.export.api.RecordExportAPI
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.plugin.RecordExporterPlugin
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.plugin.api.of
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SupportedExporters")

/**
 * An immutable list of [RecordExporter]s can be used by the other parts of the app.
 * It includes exporters collected from plugins.
 */
object SupportedExporters : List<RecordExporter<*>> by loadExporters()

private fun loadExporters() =
    loadBuiltInExporters().plus(loadExportersFromPlugins())
        .onEach { logger.debug("Found exporter '{}'", it.javaClass.name) }
        .toList()

private fun loadBuiltInExporters(): Sequence<RecordExporter<*>> =
    get(InternalExportersConfig::class).exporters.asSequence()

private fun loadExportersFromPlugins() =
    get(PluginService::class)
        .of(RecordExporterPlugin::class)
        .asSequence()
        .map(RecordExporterPlugin::exporter)