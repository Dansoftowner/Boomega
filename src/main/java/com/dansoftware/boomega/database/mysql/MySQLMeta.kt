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

package com.dansoftware.boomega.database.mysql

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseProvider

/**
 * Represents the meta-information of a mysql database
 */
class MySQLMeta : DatabaseMeta {

    @Suppress("UNCHECKED_CAST")
    override val provider: DatabaseProvider<DatabaseMeta>
        get() = MySQLProvider as DatabaseProvider<DatabaseMeta>

    /**
     * The host of the mysql server
     */
    val host: String

    /**
     * The port of the mysql server
     */
    val port: Int

    /**
     * The name of the mysql database
     */
    val databaseName: String

    /**
     * The version of the mysql database
     */
    val version: MySQLVersion

    /**
     * The host and the port of the mysql server concatenated
     */
    val socket get() = "$host:$port"

    /**
     * The full mysql uri of the database
     */
    val uri get() = "$socket/$databaseName"

    override val identifier: String get() = "$uri|$version"
    override val simpleName: String get() = databaseName

    override val supportedActions: Set<Action<*>>
        get() = setOf()

    constructor(url: String, version: MySQLVersion = MySQLVersion._8) {
        val parts = url.replace('/', ':').split(':')
        this.host = parts[0]
        this.port = parts[1].toInt()
        this.databaseName = parts[2]
        this.version = version
    }

    constructor(
        host: String,
        port: Int,
        databaseName: String,
        version: MySQLVersion
    ) {
        this.host = host
        this.port = port
        this.databaseName = databaseName
        this.version = version
    }

    override fun <T> performAction(action: Action<T>): T {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return uri
    }
}