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
import com.dansoftware.boomega.export.json.JsonExporter
import okhttp3.internal.toImmutableList
import java.util.*

/**
 * An immutable list of [RecordExporter]s can be used by the other parts of the app.
 * It includes exporters collected from plugins.
 */
object SupportedExporters :
    List<RecordExporter<*>> by LinkedList(loadBuiltInExporters() + loadExportersFromPlugins()).toImmutableList()

private fun loadBuiltInExporters() = listOf(
    JsonExporter()
)

private fun loadExportersFromPlugins() = listOf<RecordExporter<*>>() // TODO: collect from plugins
