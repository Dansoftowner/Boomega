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

package com.dansoftware.boomega.export

import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.export.excel.ExcelExporter
import com.dansoftware.boomega.export.json.JsonExporter
import com.dansoftware.boomega.export.txtable.TXTableExporter
import com.dansoftware.boomega.plugin.Plugins
import com.dansoftware.boomega.plugin.api.RecordExporterPlugin
import okhttp3.internal.toImmutableList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

private val logger: Logger = LoggerFactory.getLogger("SupportedExporters")

/**
 * An immutable list of [RecordExporter]s can be used by the other parts of the app.
 * It includes exporters collected from plugins.
 */
object SupportedExporters :
    List<RecordExporter<*>> by LinkedList(loadExporters()).toImmutableList()

private fun loadExporters() =
    loadBuiltInExporters().plus(loadExportersFromPlugins())
        .onEach { logger.debug("Found exporter '{}'", it.javaClass.name) }
        .toList()

private fun loadBuiltInExporters() = sequenceOf(
    JsonExporter(),
    TXTableExporter(),
    ExcelExporter()
)

private fun loadExportersFromPlugins() =
    Plugins.getInstance()
        .of(RecordExporterPlugin::class.java)
        .asSequence()
        .map(RecordExporterPlugin::exporter)