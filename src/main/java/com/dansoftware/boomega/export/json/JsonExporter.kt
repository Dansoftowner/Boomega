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
import com.dansoftware.boomega.db.data.RecordProperty
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.gui.export.ConfigurationPanel
import com.dansoftware.boomega.gui.export.JsonConfigurationPanel
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import com.google.gson.*
import javafx.concurrent.Task
import javafx.scene.Node
import java.io.OutputStreamWriter
import java.lang.reflect.Type

class JsonExporter : RecordExporter<JsonExportConfiguration> {

    override val contentType: String
        get() = "json"

    override val contentTypeDescription: String
        get() = i18n("file.content_type.desc.json")

    override val name: String
        get() = "JSON"

    override val icon: Node
        get() = icon("json-icon")

    override val configurationPanel: ConfigurationPanel<JsonExportConfiguration>
        get() = JsonConfigurationPanel()

    override fun getTask(items: List<Record>, config: JsonExportConfiguration): Task<Unit> = object : Task<Unit>() {
        override fun call() = write(items, config)
    }

    private fun write(items: List<Record>, config: JsonExportConfiguration) {
        OutputStreamWriter(config.outputStream).buffered().use {
            val gson = buildGson(config)
            gson.toJson(gson.toJsonTree(items, List::class.java), it)
        }
    }

    private fun buildGson(config: JsonExportConfiguration) = GsonBuilder().run {
        config.prettyPrinting.takeUnless(Boolean::not)?.let { setPrettyPrinting() }
        config.nonExecutableJson.takeUnless(Boolean::not)?.let { generateNonExecutableJson() }
        config.serializeNulls.takeUnless(Boolean::not)?.let { serializeNulls() }
        registerTypeAdapter(Record::class.java, RecordSerializer(config))
        create()
    }

    private class RecordSerializer(private val config: JsonExportConfiguration) : JsonSerializer<Record> {
        override fun serialize(src: Record, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val serialized: JsonElement = Gson().toJsonTree(src)

            // removing data the user should not see
            serialized.asJsonObject.remove("id")
            serialized.asJsonObject.remove("serviceConnection")

            // we exclude the props that are not used with the given record's type
            val requiredProps = config.requiredFields.filter { src.type in it.typeScopes }

            // the properties that should not be present in the json object
            val propsToRemove: List<RecordProperty<*>> = RecordProperty.allProperties - requiredProps
            propsToRemove.map(RecordProperty<*>::id).forEach(serialized.asJsonObject::remove)

            return serialized
        }
    }
}