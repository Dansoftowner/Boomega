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

package com.dansoftware.boomega.database.sql.mysql

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.database.sql.SQLDatabase
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.database.mysql.MySQLLoginForm
import com.dansoftware.boomega.gui.database.mysql.MySQLRegistrationForm
import com.dansoftware.boomega.gui.util.icon
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import org.hibernate.HibernateException
import org.hibernate.service.spi.ServiceException

/**
 * Represents the MySQL RDBMS.
 */
object MySQLProvider : DatabaseProvider<MySQLMeta> {

    // TODO: i18n

    val MYSQL_VERSION_FIELD = DatabaseField(
        valueType = MySQLVersion::class.java,
        id = "mysql.version",
        name = "MySQL verison"
    )

    val USERNAME_FIELD = DatabaseField(
        valueType = String::class.java,
        id = "usernm",
        name = "Username"
    )

    val PASSWORD_FIELD = DatabaseField(
        valueType = String::class.java,
        id = "psswrd",
        name = "Password"
    )

    override val name: String
        get() = "MySQL"

    override val icon: Node
        get() = icon("mysql-icon")

    override val availableOptions: List<DatabaseOption<*>>
        get() = emptyList()

    // TODO
    override val fields: List<DatabaseField<*>>
        get() = listOf(
            MYSQL_VERSION_FIELD,
            USERNAME_FIELD,
            PASSWORD_FIELD
        )

    override fun getMeta(identifier: String): MySQLMeta {
        return splitURL(identifier) { validUrl, version ->
            MySQLMeta(validUrl, version)
        }
    }

    private inline fun <T> splitURL(url: String, onCalculated: (url: String, version: MySQLVersion) -> T): T {
        val versionSeparatorIndex = url.indexOf("|")
        return onCalculated(
            url.takeIf {
                versionSeparatorIndex < 0
            } ?: url.substring(0, versionSeparatorIndex),

            MySQLVersion._8.takeIf {
                versionSeparatorIndex < 0
            } ?: MySQLVersion.find(url.substring(versionSeparatorIndex + 1, url.length))
        )
    }

    override fun getDatabase(
        meta: MySQLMeta,
        credentials: Map<DatabaseField<*>, Any?>,
        options: Map<DatabaseOption<*>, Any>
    ): Database {
        return try {
            SQLDatabase(
                meta,
                hibernateOptions = mapOf(
                    "hibernate.connection.driver_class" to "com.mysql.cj.jdbc.Driver",
                    "hibernate.dialect" to (credentials[MYSQL_VERSION_FIELD] as? MySQLVersion ?: MySQLVersion._8).hibernateDialect,
                    "hibernate.hbm2ddl.auto" to "update",
                    "hibernate.connection.url" to "jdbc:mysql://${meta.uri}",
                    "hibernate.connection.username" to credentials[USERNAME_FIELD].toString(),
                    "hibernate.connection.password" to credentials[PASSWORD_FIELD].toString()
                )
            )
        } catch(e: HibernateException) {
            throw DatabaseConstructionException(localizedMessage = e.specificMessage(), cause = e)
        }
    }

    override fun buildUILoginForm(
        context: Context,
        databaseMeta: ReadOnlyObjectProperty<MySQLMeta>,
        options: Map<DatabaseOption<*>, Any>
    ): LoginForm<MySQLMeta> {
        return MySQLLoginForm(context, databaseMeta, options)
    }

    override fun buildUIRegistrationForm(
        context: Context,
        options: Map<DatabaseOption<*>, Any>
    ): RegistrationForm<MySQLMeta> {
        return MySQLRegistrationForm(context, options)
    }

    private fun HibernateException.specificMessage(): String {
        // TODO: i18n
        return when(this) {
            is ServiceException -> "Unable to create connection with the server"
            else -> "Failed to log into the database"
        }
    }

}