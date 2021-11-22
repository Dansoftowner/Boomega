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

package com.dansoftware.boomega.gui.database.mysql

import com.dansoftware.boomega.database.api.DatabaseOption
import com.dansoftware.boomega.database.api.RegistrationForm
import com.dansoftware.boomega.database.mysql.MySQLMeta
import com.dansoftware.boomega.database.mysql.MySQLVersion
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.selectedItemProperty
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class MySQLRegistrationForm(
    context: Context,
    options: Map<DatabaseOption<*>, Any>
) : RegistrationForm<MySQLMeta>(context, options) {

    private val host = SimpleStringProperty()
    private val port = SimpleStringProperty()
    private val databaseName = SimpleStringProperty()
    private val version = SimpleObjectProperty<MySQLVersion>()

    private val socket get() = "${host.get()}:${port.get()}/${databaseName.get()}"

    init {
        buildUI()
    }

    // TODO: grid pane structure
    private fun buildUI() {
        children.add(
            VBox(5.0,
                HBox(5.0, Label("Host:"), buildHostField()),
                HBox(5.0, Label("Port:"), buildPortField()),
                HBox(5.0, Label("Database name:"), buildDatabaseNameField()),
                HBox(5.0, Label("Version:"), buildVersionChooser())
            )
        )
    }

    private fun buildHostField() = TextField().apply {
        // TODO: prompt text
        // TODO: validation
        Bindings.bindBidirectional(textProperty(), host)
    }

    private fun buildPortField() = TextField().apply {
        // TODO: prompt
        // TODO: validation
        Bindings.bindBidirectional(textProperty(), port)
    }

    private fun buildDatabaseNameField() = TextField().apply {
        Bindings.bindBidirectional(textProperty(), databaseName)
    }

    private fun buildVersionChooser() = ComboBox<MySQLVersion>().apply {
        items.addAll(MySQLVersion.values())
        selectionModel.select(0)
        version.bind(selectedItemProperty())
    }

    override fun registrate(): MySQLMeta {
        return MySQLMeta(socket, version.get())
    }
}