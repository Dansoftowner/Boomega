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

package com.dansoftware.boomega.gui.login.config

import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.google.gson.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

class LoginDataSerializer : JsonSerializer<LoginData> {

    override fun serialize(
        src: LoginData,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        logger.debug("Serializing login-data....")

        // { "svdbs" : [ ... ], "autolgn": true,  "scltdb": 2, "crdntls": { ... } }
        val json = JsonObject()
        json.add(SAVED_DATABASES, serializeDatabases(src.savedDatabases.toList()))
        json.addProperty(AUTO_LOGIN, src.isAutoLogin)
        json.addProperty(SELECTED_DATABASE_INDEX, src.selectedDatabaseIndex)
        json.add(
            AUTO_LOGIN_CREDENTIALS,
            src.autoLoginCredentials
                ?.takeIf { src.isAutoLogin }
                ?.let { serializeCredentials(context, it) }
        )
        return json
    }

    private fun serializeDatabases(savedDatabases: List<DatabaseMeta>): JsonArray {
        // [ { "provider" : "<CLASS>", "dburl" : "<FILE PATH>" }, ... ]
        val array = JsonArray()
        val serializedEntries = savedDatabases.map {
            JsonObject().apply {
                addProperty(DATABASE_PROVIDER, it.provider.javaClass.name)
                addProperty(DATABASE_URL, it.url)
            }
        }
        serializedEntries.forEach(array::add)
        return array
    }

    private fun serializeCredentials(
        context: JsonSerializationContext,
        autoLoginCredentials: Map<DatabaseField<*>, Any>
    ): JsonObject {
        // { "field" : <VALUE>, "field1" : <VALUE>, ... }
        //TODO: encryption
        val jsonObject = JsonObject()
        autoLoginCredentials.forEach { (key, value) ->
            jsonObject.add(key.id, context.serialize(value))
        }
        return jsonObject
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(LoginDataSerializer::class.java)

        private const val SAVED_DATABASES = "svdbs_2_0"
        private const val SELECTED_DATABASE_INDEX = "slctdb"
        private const val AUTO_LOGIN = "autolgn"
        private const val AUTO_LOGIN_CREDENTIALS = "crdntls_2_0"
        private const val DATABASE_PROVIDER = "provider"
        private const val DATABASE_URL = "dburl"
    }
}