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

package com.dansoftware.boomega.database.sql

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node

object MySQLProvider : DatabaseProvider<MySQLMeta> {

    // TODO: i18n

    // TODO: username, password

    val MYSQL_VERSION_FIELD = DatabaseField(
        Version::class.java,
        "mysql.version",
        "MySQL verison"
    )

    override val name: String
        get() = "MySQL"

    override val icon: Node
        get() = icon("database-icon")

    override val availableOptions: List<DatabaseOption<*>>
        get() = emptyList()

    // TODO
    override val fields: List<DatabaseField<*>>
        get() = emptyList()

    override fun getMeta(url: String): MySQLMeta {
        return MySQLMeta(url)
    }

    override fun getDatabase(
        meta: MySQLMeta,
        credentials: Map<DatabaseField<*>, Any?>,
        options: Map<DatabaseOption<*>, Any>
    ): Database {
        return SQLDatabase(
            meta,
            hibernateOptions = mapOf(
                "hibernate.connection.driver_class" to "com.mysql.cj.jdbc.Driver",
                "hibernate.dialect" to (credentials[MYSQL_VERSION_FIELD] as Version).hibernateDialect,
                "hibernate.hbm2ddl.auto" to "update",
                "hibernate.connection.url" to "jdbc:mysql://${meta.url}",
                // TODO: username, password
            )
        )
    }

    override fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<MySQLMeta>,
        options: Map<DatabaseOption<*>, Any>
    ): LoginForm<MySQLMeta> {
        TODO("Not yet implemented")
    }

    override fun buildUIRegistrationForm(
        context: Context,
        options: Map<DatabaseOption<*>, Any>
    ): RegistrationForm<MySQLMeta> {
        TODO("Not yet implemented")
    }

    enum class Version(val hibernateDialect: String) {

        /**
         * Represents MySQL versions prior to 5.x
         */
        _5_X_PRIOR("org.hibernate.dialect.MySQLDialect"),

        /**
         * Represents MySQL 5.x
         */
        _5_X("org.hibernate.dialect.MySQL5Dialect"),

        /**
         * Represents MySQL 5.5
         */
        _5_5("org.hibernate.dialect.MySQL55Dialect"),

        /**
         * Represents MySQL 5.7
         */
        _5_7("org.hibernate.dialect.MySQL57Dialect"),

        /**
         * Represents MySQL 8.x+
         */
        _8("org.hibernate.dialect.MySQL8Dialect")

    }
}