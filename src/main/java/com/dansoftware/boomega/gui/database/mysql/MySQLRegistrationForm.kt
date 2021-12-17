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
import com.dansoftware.boomega.database.sql.mysql.MySQLMeta
import com.dansoftware.boomega.database.sql.mysql.MySQLVersion
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.NumberTextField
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.gui.util.validateImmediate
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.isValidHostAddress
import com.dansoftware.boomega.util.isValidPortNumber
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import net.synedra.validatorfx.Validator

/**
 * Registration form for mysql
 */
class MySQLRegistrationForm(
    context: Context,
    options: Map<DatabaseOption<*>, Any>
) : RegistrationForm<MySQLMeta>(context, options) {

    private val host = SimpleStringProperty()
    private val port = SimpleStringProperty()
    private val databaseName = SimpleStringProperty()
    private val version = SimpleObjectProperty<MySQLVersion>()

    private val validator = Validator()

    override val persistable: ObservableBooleanValue =
        host.isNotEmpty.and(port.isNotEmpty).and(databaseName.isNotEmpty)

    override val node: Node = Grid()

    private inner class Grid : GridPane() {

        init {
            padding = Insets(10.0)
            hgap = 5.0
            vgap = 5.0
            buildUI()
        }

        private fun buildUI() {
            children.addAll(
                buildLabel("database.creator.sql.host", 0, 0),
                buildHostField(),
                buildLabel("database.creator.sql.port", 1, 0),
                buildPortField(),
                buildLabel("database.creator.sql.name", 0, 2),
                buildNameField(),
                buildLabel("database.creator.mysql.version", 1, 2),
                buildVersionChooser()
            )
        }

        private fun buildLabel(i18n: String, column: Int, row: Int) = Label(i18n(i18n)).apply {
            setConstraints(this, column, row)
        }

        private fun buildHostField() = TextField().apply {
            setConstraints(this, 0, 1)
            setHgrow(this, Priority.SOMETIMES)
            Bindings.bindBidirectional(host, textProperty())
            promptText = i18n("database.creator.sql.host.prompt")
            minHeight = 35.0
            validateImmediate(validator, { isValidHostAddress(it).not() }) {
                it.warn("Invalid host address!") // TODO: i18n
            }
        }

        private fun buildPortField() = NumberTextField().apply {
            setConstraints(this, 1, 1)
            Bindings.bindBidirectional(port, textProperty())
            promptText = i18n("database.creator.sql.port.prompt")
            minHeight = 35.0

            validateImmediate(validator, { isValidPortNumber(it).not() }) {
                it.warn("Invalid port!") // TODO: i18n
            }
        }

        private fun buildNameField() = TextField().apply {
            setConstraints(this, 0, 3)
            setHgrow(this, Priority.SOMETIMES)
            Bindings.bindBidirectional(databaseName, textProperty())
            promptText = i18n("database.creator.sql.name.prompt")
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