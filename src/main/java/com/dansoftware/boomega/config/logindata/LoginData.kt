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

package com.dansoftware.boomega.config.logindata

import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseMeta
import javafx.collections.ObservableList

class LoginData(
    val savedDatabases: ObservableList<DatabaseMeta>,
    selectedDatabase: DatabaseMeta,
    autoLoginCredentials: Map<DatabaseField, String>?
) {
    var selectedDatabase: DatabaseMeta = selectedDatabase
        set(value) {
             if (value !in savedDatabases)
                 savedDatabases.add(selectedDatabase)
            field = value
        }

    var autoLoginCredentials: Map<DatabaseField, String>? = autoLoginCredentials
        get() = field ?: emptyMap()

    var selectedDatabaseIndex: Int
        get() = savedDatabases.indexOf(selectedDatabase)
        set(value) {
            selectedDatabase = savedDatabases[value]
        }

    val isAutoLogin: Boolean get() = autoLoginCredentials !== null

    override fun toString(): String {
        return "LoginData(savedDatabases=$savedDatabases, selectedDatabase=$selectedDatabase, isAutoLogin=$isAutoLogin)"
    }
}