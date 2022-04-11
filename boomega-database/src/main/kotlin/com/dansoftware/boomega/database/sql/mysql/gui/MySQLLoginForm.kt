/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.database.sql.mysql.gui

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseOption
import com.dansoftware.boomega.database.api.LoginForm
import com.dansoftware.boomega.database.sql.mysql.MySQLMeta
import com.dansoftware.boomega.database.sql.mysql.MySQLProvider
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.api.i18n
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class MySQLLoginForm(
    context: Context,
    databaseMeta: ReadOnlyObjectProperty<MySQLMeta>,
    options: Map<DatabaseOption<*>, Any>
) : LoginForm<MySQLMeta>(context, databaseMeta, options) {

    private val username: StringProperty = SimpleStringProperty()
    private val password: StringProperty = SimpleStringProperty()

    private val fields: Map<DatabaseField<*>, Any>
        get() = mapOf(
            MySQLProvider.USERNAME_FIELD to username.get(),
            MySQLProvider.PASSWORD_FIELD to password.get(),
            MySQLProvider.MYSQL_VERSION_FIELD to databaseMeta.get().version
        )

    override val node: Node
        get() = VBox(5.0).apply {
            children.add(buildUsernameInput())
            children.add(buildPasswordInput())
        }


    private fun buildUsernameInput() = TextField().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        minHeight = 35.0
        prefColumnCount = 10
        promptText = i18n("credentials.username")
        Bindings.bindBidirectional(textProperty(), username)
    }

    private fun buildPasswordInput() = PasswordField().apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        minHeight = 35.0
        prefColumnCount = 10
        promptText = i18n("credentials.password")
        Bindings.bindBidirectional(textProperty(), password)
    }

    override fun login(): Database {
        return MySQLProvider.getDatabase(databaseMeta.get(), fields, options)
    }
}