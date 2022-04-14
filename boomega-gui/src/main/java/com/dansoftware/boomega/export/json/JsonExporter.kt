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

package com.dansoftware.boomega.export.json

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.database.api.data.RecordProperty
import com.dansoftware.boomega.database.api.data.ServiceConnection
import com.dansoftware.boomega.export.api.BaseExporter
import com.dansoftware.boomega.export.api.ExportProcessObserver
import com.dansoftware.boomega.gui.export.ConfigurationDialog
import com.dansoftware.boomega.gui.export.json.JsonConfigurationDialog
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.minus
import com.dansoftware.boomega.util.set
import com.google.gson.*
import javafx.scene.Node
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.reflect.Type

/**
 * A [JsonExporter] allows to export [Record]s into json format.
 */
class JsonExporter : BaseExporter<JsonExportConfiguration>() {

    override val contentType: String
        get() = "json"

    override val contentTypeDescription: String
        get() = i18n("file.content_type.desc.json")

    override val name: String
        get() = "JSON"

    override val icon: Node
        get() = icon("json-icon")

    override val configurationDialog: ConfigurationDialog<JsonExportConfiguration>
        get() = JsonConfigurationDialog()

    override fun write(
        items: List<Record>,
        output: OutputStream,
        config: JsonExportConfiguration,
        observer: ExportProcessObserver
    ) {
        OutputStreamWriter(output).buffered().use {
            val gson = buildGson(config)
            gson.toJson(gson.toJsonTree(sortRecords(items, config), List::class.java), it)
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

        private val internalGson = GsonBuilder().run {
            config.serializeNulls.takeUnless(Boolean::not)?.let { serializeNulls() }
            registerTypeAdapter(ServiceConnection::class.java, ServiceConnectionSerializer())
            create()
        }

        override fun serialize(src: Record, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val serialized = internalGson.toJsonTree(src) as JsonObject

            // removing data the user should not see
            serialized - "id"

            // we exclude the props that are not used with the given record's type
            val requiredProps = config.requiredFields.filter { src.type in it.typeScopes }

            // the properties that should not be present in the json object
            val propsToRemove: List<RecordProperty<*>> = RecordProperty.allProperties - requiredProps
            propsToRemove.map(RecordProperty<*>::id).forEach(serialized.asJsonObject::remove)

            return serialized
        }
    }

    private class ServiceConnectionSerializer() : JsonSerializer<ServiceConnection> {
        override fun serialize(
            src: ServiceConnection,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return when {
                src.entries().isEmpty() -> JsonNull.INSTANCE
                else -> JsonObject().also { jsonObject ->
                    src.entries().forEach {
                        jsonObject[it.first] = it.second
                    }
                }
            }
        }
    }
}