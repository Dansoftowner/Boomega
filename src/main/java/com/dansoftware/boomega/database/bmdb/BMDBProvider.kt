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

import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseOption
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import javafx.scene.layout.Region

object BMDBProvider : DatabaseProvider {


    // TODO: i18n
    override val name: String
        get() = "BMDB local database"

    override val icon: Node
        get() = icon("file-icon")

    override val availableOptions: List<DatabaseOption>
        get() = TODO("Not yet implemented")


    override val fields: List<DatabaseField>
        get() = TODO("Not yet implemented")

    override fun getMeta(url: String): DatabaseMeta {
        TODO("Not yet implemented")
    }

    override fun getDatabase(credentials: Map<DatabaseField, Any?>, options: List<DatabaseOption>): Database {
        TODO("Not yet implemented")
    }

    override fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<DatabaseMeta>,
        options: List<DatabaseOption>,
        onDatabaseAuthenticated: (Database) -> Unit
    ): Region {
        TODO("Not yet implemented")
    }

    override fun buildUIRegistrationForm(
        context: Context,
        options: List<DatabaseOption>,
        onDatabaseCreated: (DatabaseMeta) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}