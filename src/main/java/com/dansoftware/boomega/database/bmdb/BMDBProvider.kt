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
import com.dansoftware.boomega.gui.util.icon
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import java.io.File

object BMDBProvider : DatabaseProvider<BMDBMeta> {

    val USERNAME_FIELD = DatabaseField()
    val PASSWORD_FIELD = DatabaseField()

    // TODO: i18n
    val COMPRESSED = DatabaseOption(name = "Compress", type = Boolean::class.java, defaultValue = true)
    val AUTO_COMMIT_BUFFER_SIZE = DatabaseOption(name = "Auto commit buffer size", type = Int::class.java, defaultValue = 1024)

    // TODO: i18n
    override val name: String
        get() = "BMDB local database"

    override val icon: Node
        get() = icon("file-icon")

    override val availableOptions: List<DatabaseOption<*>> = listOf(
        COMPRESSED,
        AUTO_COMMIT_BUFFER_SIZE
    )

    override val fields: List<DatabaseField> = listOf(
        USERNAME_FIELD,
        PASSWORD_FIELD
    )

    override fun getMeta(url: String): DatabaseMeta {
        return BMDBMeta(File(url))
    }

    override fun getDatabase(
        meta: BMDBMeta,
        credentials: Map<DatabaseField, Any?>,
        options: Map<DatabaseOption<*>, Any>
    ): Database {
        return BMDBDatabase(
            credentials[USERNAME_FIELD].toString(),
            credentials[PASSWORD_FIELD].toString(),
            meta,
            isCompressed = COMPRESSED.getValueFrom(options),
            autoCommitBufferSize = AUTO_COMMIT_BUFFER_SIZE.getValueFrom(options)
        )
    }

    override fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<BMDBMeta>,
        options: Map<DatabaseOption<*>, Any>
    ): LoginForm {
        TODO("Not yet implemented")
    }

    override fun buildUIRegistrationForm(
        context: Context,
        options: Map<DatabaseOption<*>, Any>
    ): RegistrationForm {
        TODO("Not yet implemented")
    }
}