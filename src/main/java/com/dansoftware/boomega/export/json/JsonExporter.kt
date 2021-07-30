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

package com.dansoftware.boomega.export.json

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.export.ConfigurationPanel
import com.google.gson.GsonBuilder
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.concurrent.Task
import java.io.OutputStreamWriter

class JsonExporter : RecordExporter<JsonExportConfiguration> {

    override val contentType: String
        get() = "json"

    override val contentTypeDescription: String
        get() = "JSON" // TODO: i18n

    override val name: String
        get() = "JSON" // TODO: i18n

    override val icon: MaterialDesignIcon
        get() = MaterialDesignIcon.JSON

    override val configurationPanel: ConfigurationPanel<JsonExportConfiguration>
        get() = object : ConfigurationPanel<JsonExportConfiguration> {
            override fun show(context: Context, onFinished: (JsonExportConfiguration) -> Unit) {
                onFinished(JsonExportConfiguration())
            }
        } // TODO : json config panel

    override fun getTask(items: List<Record>, config: JsonExportConfiguration): Task<Unit> = object : Task<Unit>() {
        override fun call() = write(items, config)
    }

    private fun write(items: List<Record>, config: JsonExportConfiguration) {
        OutputStreamWriter(config.outputStream).buffered().use {
            val gson = GsonBuilder().run {
                if (config.isPrettyPrinting) setPrettyPrinting()
                create()
            }
            gson.toJson(gson.toJsonTree(items, List::class.java), it)
        }
    }
}