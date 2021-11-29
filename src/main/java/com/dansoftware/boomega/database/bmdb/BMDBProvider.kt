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

package com.dansoftware.boomega.database.bmdb

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.database.bmdb.BMDBLoginForm
import com.dansoftware.boomega.gui.database.bmdb.BMDBRegistrationForm
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import org.dizitart.no2.exceptions.ErrorMessage
import org.dizitart.no2.exceptions.NitriteException
import org.dizitart.no2.exceptions.NitriteIOException
import java.io.File

/**
 * Represents the internal Boomega database management system.
 */
object BMDBProvider : DatabaseProvider<BMDBMeta> {

    // TODO: i18n

    val USERNAME_FIELD = DatabaseField(
        valueType = String::class.java,
        id = "usrnm",
        name = "Username"
    )

    val PASSWORD_FIELD = DatabaseField(
        valueType = String::class.java,
        id = "psswrd",
        name = "Password"
    )

    val COMPRESSED = DatabaseOption(
        id = "cmprsd",
        name = "Compress",
        type = Boolean::class.java,
        defaultValue = true
    )

    val AUTO_COMMIT_BUFFER_SIZE = DatabaseOption(
        id = "bfs",
        name = "Auto commit buffer size",
        type = Int::class.java,
        defaultValue = 1024
    )

    override val name: String
        get() = "BMDB"

    override val icon: Node
        get() = icon("bmdb-icon")

    override val availableOptions: List<DatabaseOption<*>> = listOf(
        COMPRESSED,
        AUTO_COMMIT_BUFFER_SIZE
    )

    override val fields: List<DatabaseField<*>> = listOf(
        USERNAME_FIELD,
        PASSWORD_FIELD
    )

    override fun getMeta(identifier: String): BMDBMeta {
        return BMDBMeta(File(identifier))
    }

    override fun getDatabase(
        meta: BMDBMeta,
        credentials: Map<DatabaseField<*>, Any?>,
        options: Map<DatabaseOption<*>, Any>
    ): Database {
        try {
            return BMDBDatabase(
                credentials[USERNAME_FIELD]?.toString()?.takeIf { it.isNotBlank() },
                credentials[PASSWORD_FIELD]?.toString()?.takeIf { it.isNotBlank() },
                meta,
                isCompressed = COMPRESSED.getValueFrom(options),
                autoCommitBufferSize = AUTO_COMMIT_BUFFER_SIZE.getValueFrom(options)
            )
        } catch (e: NitriteException) {
            throw DatabaseConstructionException(localizedMessage = e.specificMessage(), cause = e)
        }

    }

    override fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<BMDBMeta>,
        options: Map<DatabaseOption<*>, Any>
    ): LoginForm<BMDBMeta> {
        return BMDBLoginForm(context, databaseMeta, options)
    }

    override fun buildUIRegistrationForm(
        context: Context,
        options: Map<DatabaseOption<*>, Any>
    ): RegistrationForm<BMDBMeta> {
        return BMDBRegistrationForm(context, options)
    }

    @Suppress("NullableBooleanElvis")
    private fun NitriteException.specificMessage(): String {
        return i18n(
            when (errorMessage) {
                ErrorMessage.NO_USER_MAP_FOUND -> "login.failed.null_user_credential"
                ErrorMessage.USER_MAP_SHOULD_NOT_EXISTS -> "login.failed.authentication_required"
                ErrorMessage.DATABASE_OPENED_IN_OTHER_PROCESS -> "login.failed.database_opened_in_other_process"
                ErrorMessage.UNABLE_TO_CREATE_DB_FILE -> "login.failed.unable_to_create_db_file"

                ErrorMessage.USER_ID_IS_EMPTY,
                ErrorMessage.PASSWORD_IS_EMPTY,
                ErrorMessage.NULL_USER_CREDENTIAL,
                ErrorMessage.INVALID_USER_PASSWORD -> "login.failed.invalid_user_password"

                else -> if (this is NitriteIOException) "login.failed.io" else "login.failed.security"
            }
        )
    }
}