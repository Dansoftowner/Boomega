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

package com.dansoftware.boomega.gui.database.bmdb

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseOption
import com.dansoftware.boomega.database.api.LoginForm
import com.dansoftware.boomega.database.bmdb.BMDBMeta
import com.dansoftware.boomega.database.bmdb.BMDBProvider
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.PasswordField
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.layout.VBox

class BMDBLoginForm(
    context: Context,
    databaseMeta: ReadOnlyObjectProperty<BMDBMeta>,
    options: Map<DatabaseOption<*>, Any>
) : LoginForm<BMDBMeta>(context, databaseMeta, options) {

    private val username: StringProperty = SimpleStringProperty()
    private val password: StringProperty = SimpleStringProperty()

    override val fields: Map<DatabaseField<*>, Any>
        get() = mapOf(
            BMDBProvider.USERNAME_FIELD to username.get(),
            BMDBProvider.PASSWORD_FIELD to password.get()
        )

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(
            VBox().apply {
                children.add(Separator())
                children.add(buildUsernameInput())
                children.add(buildPasswordInput())
                VBox.setMargin(this, Insets(0.0, 20.0, 20.0, 20.0))
            }
        )
    }

    private fun buildUsernameInput() = TextField().apply {
        minHeight = 35.0
        prefColumnCount = 10
        promptText = i18n("credentials.username")
        Bindings.bindBidirectional(textProperty(), username)
    }

    private fun buildPasswordInput() = PasswordField().apply {
        minHeight = 35.0
        prefColumnCount = 10
        promptText = i18n("credentials.password")
        Bindings.bindBidirectional(textProperty(), password)
    }

    override fun login(): Database {
        return BMDBProvider.getDatabase(databaseMeta.get(), fields, options)
    }

}