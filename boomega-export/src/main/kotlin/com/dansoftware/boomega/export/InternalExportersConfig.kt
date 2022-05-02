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

package com.dansoftware.boomega.export

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.util.resJson
import com.google.gson.JsonArray
import com.google.inject.ImplementedBy
import com.google.inject.Singleton

/**
 * The entity representing the internal-exporters config file.
 * Gives the list of record-exporters should be loaded by default.
 */
@ImplementedBy(DefaultInternalExportersConfig::class)
internal open class InternalExportersConfig(json: JsonArray) {

    /**
     * Gives the exporter class-names read from the config
     */
    val exporterClassNames: List<String> by lazy {
        json.map { it.asString }
    }

    /**
     * Gives all the instantiated exporters registered in the config
     */
    val exporters: List<RecordExporter<*>> by lazy {
        exporterClassNames
            .map { Class.forName(it) }
            .map { get(it) }
            .filterIsInstance<RecordExporter<*>>()
    }

    /**
     * To allow subclasses reference 'this' in the super constructor
     */
    protected companion object
}

/**
 * The record-exporter configuration object reads from the default configuration file
 */
@Singleton
private class DefaultInternalExportersConfig :
    InternalExportersConfig(resJson("internal_exporters.json", this::class).asJsonArray)