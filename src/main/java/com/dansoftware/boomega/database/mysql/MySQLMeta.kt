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
    override val name: String

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
    override val uri get() = "$socket/$name"

    /**
     * The unique identifier of the mysql database.
     *
     * The difference between this and the [uri] is that it
     * also encodes the version of the mysql database besides the uri.
     */
    override val identifier: String get() = "$uri|$version"

    override val supportedActions: Set<Action<*>>
        get() = setOf()

    constructor(
        uri: String,
        version: MySQLVersion = MySQLVersion._8
    ) : this(uri.split(":", "/"), version)

    private constructor(
        parts: List<String>,
        version: MySQLVersion
    ) : this(
        parts[0],
        parts[1].toInt(),
        parts[2],
        version
    )

    constructor(
        host: String,
        port: Int,
        name: String,
        version: MySQLVersion
    ) {
        this.host = host
        this.port = port
        this.name = name
        this.version = version
    }

    override fun <T> performAction(action: Action<T>): T {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return uri
    }
}