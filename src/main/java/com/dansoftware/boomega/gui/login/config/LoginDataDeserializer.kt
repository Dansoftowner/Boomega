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

import com.dansoftware.boomega.database.SupportedDatabases
import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.google.gson.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type

class LoginDataDeserializer : JsonDeserializer<LoginData> {

    override fun deserialize(jsonSrc: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LoginData {
        logger.debug("Deserializing login data...")

        val json = jsonSrc.asJsonObject
        val databases = getSavedDatabases(json)
        val isAutoLogin = getAutoLogin(json)
        val selectedDatabaseIndex = getSelectedDatabaseIndex(json)
        val selectedDatabase = selectedDatabaseIndex?.let { databases[it] }
        val autoLoginCredentials = getAutoLoginCredentials(context, json, selectedDatabase, isAutoLogin)

        logger.debug("Found {} saved database(s)", databases.size)
        logger.debug("Auto login: {}", isAutoLogin)
        logger.debug("Selected database index: {}", selectedDatabaseIndex)

        return LoginData(databases, selectedDatabase, autoLoginCredentials, isAutoLogin)
    }

    private fun deserializeDatabases(jsonArray: JsonArray?): List<DatabaseMeta> {
        return jsonArray?.map(JsonElement::getAsJsonObject)?.map {
            val provider = getDatabaseProvider(it[DATABASE_PROVIDER].asString)
            provider!!.getMeta(it[DATABASE_URL].asString)
        } ?: emptyList()
    }

    private fun deserializeCredentials(
        context: JsonDeserializationContext,
        provider: DatabaseProvider<*>,
        jsonObject: JsonObject
    ): Map<DatabaseField<*>, Any> {
        return jsonObject.entrySet().associate { (key, value) ->
            // TODO: decryption
            val databaseField = provider.fields.find { it.id == key }!!
            val fieldValue = context.deserialize<Any>(value, databaseField.valueType)
            databaseField to fieldValue
        }
    }

    private fun getDatabaseProvider(className: String) =
        SupportedDatabases.find { it.javaClass.name == className }

    private fun getSavedDatabases(json: JsonObject) =
        deserializeDatabases(
            json[SAVED_DATABASES]
                ?.takeUnless { it.isJsonNull }
                ?.asJsonArray
        )

    private fun getAutoLogin(json: JsonObject) =
        json[AUTO_LOGIN]
            ?.takeUnless { it.isJsonNull }
            ?.asBoolean
            ?: false

    private fun getSelectedDatabaseIndex(json: JsonObject) =
        json[SELECTED_DATABASE_INDEX]
            ?.takeUnless { it.isJsonNull }
            ?.asInt
            ?.takeUnless { it == -1 }

    private fun getAutoLoginCredentials(
        context: JsonDeserializationContext,
        json: JsonObject,
        selectedDatabase: DatabaseMeta?,
        isAutoLogin: Boolean
    ) = selectedDatabase?.let {
        json[AUTO_LOGIN_CREDENTIALS]
            ?.takeUnless { it.isJsonNull }
            ?.asJsonObject
            ?.takeIf { isAutoLogin }
            ?.let {
                deserializeCredentials(
                    context,
                    selectedDatabase.provider,
                    it
                )
            }
    }

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(LoginDataDeserializer::class.java)

        private const val SAVED_DATABASES = "svdbs_2_0"
        private const val SELECTED_DATABASE_INDEX = "slctdb"
        private const val AUTO_LOGIN = "autolgn"
        private const val AUTO_LOGIN_CREDENTIALS = "crdntls_2_0"
        private const val DATABASE_PROVIDER = "provider"
        private const val DATABASE_URL = "dburl"
    }
}