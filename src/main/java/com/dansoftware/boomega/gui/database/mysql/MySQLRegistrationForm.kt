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
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.isValidHostAddress
import com.dansoftware.boomega.util.isValidPortNumber
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

// TODO: i18n
class MySQLRegistrationForm(
    context: Context,
    options: Map<DatabaseOption<*>, Any>
) : RegistrationForm<MySQLMeta>(context, options) {

    private val host = SimpleStringProperty()
    private val port = SimpleStringProperty()
    private val databaseName = SimpleStringProperty()
    private val version = SimpleObjectProperty<MySQLVersion>()

    override val node: Node
        get() = Grid()


    private inner class Grid : GridPane() {
        init {
            padding = Insets(10.0)
            hgap = 5.0
            vgap = 5.0
            buildUI()
        }

        private fun buildUI() {
            children.add(buildLabel("Host:", 0, 0))
            children.add(buildHostField())
            children.add(buildLabel("Port:", 1, 0))
            children.add(buildPortField())
            children.add(buildLabel("Database name:", 0, 2))
            children.add(buildNameField())
            children.add(buildLabel("MySQL version:", 1, 2))
            children.add(buildVersionChooser())
        }

        private fun buildLabel(i18n: String, column: Int, row: Int) = Label(i18n(i18n)).apply {
            setConstraints(this, column, row)
        }

        private fun buildHostField() = TextField().apply {
            setConstraints(this, 0, 1)
            setHgrow(this, Priority.SOMETIMES)
            textProperty().addListener { _, _, newValue ->
                pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), isValidHostAddress(newValue).not())
            }
            Bindings.bindBidirectional(host, textProperty())
            promptText = "e.g. localhost, remotemysql.com, 193.121.203.12"
            minHeight = 35.0
        }

        private fun buildPortField() = TextField().apply {
            setConstraints(this, 1, 1)
            Bindings.bindBidirectional(port, textProperty())
            textProperty().addListener { _, _, newValue ->
                pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), isValidPortNumber(newValue).not())
            }
            promptText = "e.g. 3306"
            minHeight = 35.0
        }

        private fun buildNameField() = TextField().apply {
            setConstraints(this, 0, 3)
            setHgrow(this, Priority.SOMETIMES)
            Bindings.bindBidirectional(databaseName, textProperty())
            promptText = "e.g. MyDatabase"
            minHeight = 35.0
        }

        private fun buildVersionChooser() = ComboBox<MySQLVersion>().apply {
            setConstraints(this, 1, 3)
            maxWidth = Double.MAX_VALUE
            items.addAll(MySQLVersion.values())
            selectionModel.select(MySQLVersion.default)
            version.bind(selectedItemProperty())
        }
    }

    override fun registrate(): MySQLMeta {
        return MySQLMeta(host.get(), port.get().toInt(), databaseName.get(), version.get())
    }
}