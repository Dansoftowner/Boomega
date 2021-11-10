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

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.database.bmdb.BMDBMeta
import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.SpaceValidator
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.window
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.hasValidPath
import com.dansoftware.boomega.util.shortenedPath
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import java.io.File
import java.lang.IllegalStateException

class BMDBRegistrationForm(context: Context, options: Map<DatabaseOption<*>, Any>) : RegistrationForm<BMDBMeta>(context, options) {

    private val databaseName: StringProperty = SimpleStringProperty()
    private val databaseDir: StringProperty = SimpleStringProperty()
    private val authentication: BooleanProperty = SimpleBooleanProperty()
    private val username: StringProperty = SimpleStringProperty()
    private val password: StringProperty = SimpleStringProperty()
    private val passwordRepeat: StringProperty = SimpleStringProperty()
    private val fullPath: StringProperty = SimpleStringProperty().apply {
        bind(
            databaseDir.concat(File.separator)
                .concat(databaseName)
                .concat(".")
                .concat(System.getProperty("boomega.file.extension"))
        )
    }

    private val databaseFile get() = File(fullPath.get())

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(
            Grid()
        )
    }

    override fun registrate(): BMDBMeta {
        return BMDBMeta(databaseName.get(), databaseFile)
    }

    private inner class Grid : GridPane() {

        private val directoryChooser = DirectoryChooser()

        init {
            padding = Insets(10.0)
            hgap = 5.0
            vgap = 5.0
            buildUI()
        }

        private fun buildUI() {
            children.add(buildLabel("database.creator.db_name", 0, 0))
            children.add(buildNameField())
            children.add(buildLabel("database.creator.db_dir", 1, 0))
            children.add(buildDirField())
            children.add(buildDirOpenButton())
            children.add(buildLabel("database.creator.full_path", 0, 2))
            children.add(buildFullPathField())
            children.add(buildAuthenticationCheck())
            children.add(buildUsernameInput())
            children.add(buildPasswordInput())
            children.add(buildRepeatPasswordInput())
        }

        private fun buildSeparator(row: Int) = Separator().apply {
            setRowIndex(this, row)
            setColumnSpan(this, 3)
        }

        private fun buildLabel(i18n: String, column: Int, row: Int) = Label(i18n(i18n)).apply {
            setConstraints(this, column, row)
        }

        private fun buildNameField() = TextField().apply {
            setConstraints(this, 0, 1)
            setHgrow(this, Priority.SOMETIMES)
            databaseName.bind(textProperty())
            minHeight = 35.0
            textFormatter = SpaceValidator()
        }

        private fun buildDirField() = TextField().apply {
            setConstraints(this, 1, 1)
            setHgrow(this, Priority.ALWAYS)
            minHeight = 35.0
            text = System.getProperty("boomega.dir.default.path");
            databaseDir.bindBidirectional(textProperty())
            textProperty().bindBidirectional(databaseDir)
        }

        private fun buildDirOpenButton() = Button().apply {
            setConstraints(this, 2, 1)
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = icon("folder-open-icon")
            minHeight = 35.0
            tooltip = Tooltip(i18n("data.source.adder.choose.dir"))
            setOnAction { openDirectory() }
        }

        private fun buildFullPathField() = TextField().apply {
            setConstraints(this, 0, 3)
            setColumnSpan(this, 3)
            textProperty().bind(fullPath)
            minHeight = 35.0
            isEditable = false
        }

        private fun buildAuthenticationCheck() = CheckBox(i18n("database.creator.db_auth")).apply {
            setConstraints(this, 0, 5)
            setColumnSpan(this, 3)
            setHgrow(this, Priority.SOMETIMES)
            isSelected = true
            authentication.bind(selectedProperty())
        }

        private fun buildUsernameInput() = TextField().apply {
            setMargin(this, Insets(5.0, 0.0, 0.0, 0.0))
            setConstraints(this, 0, 6)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = i18n("credentials.username")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            username.bind(textProperty())
        }

        private fun buildPasswordInput() = PasswordField().apply {
            setConstraints(this, 0, 7)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = i18n("credentials.password")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            password.bind(textProperty())
        }

        private fun buildRepeatPasswordInput() = PasswordField().apply {
            setConstraints(this, 0, 8)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = i18n("database.creator.password.repeat")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            passwordRepeat.bind(textProperty())
        }

        private fun openDirectory() {
            directoryChooser.showDialog(this.window)?.let { dir ->
                databaseDir.set(dir.absolutePath)
            }
        }
    }
}