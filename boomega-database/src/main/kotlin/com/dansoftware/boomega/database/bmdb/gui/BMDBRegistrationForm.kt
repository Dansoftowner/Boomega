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

package com.dansoftware.boomega.database.bmdb.gui

import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseOption
import com.dansoftware.boomega.database.api.RegistrationForm
import com.dansoftware.boomega.database.bmdb.BMDBMeta
import com.dansoftware.boomega.database.bmdb.BMDBProvider
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.hasValidPath
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import net.synedra.validatorfx.Validator
import java.io.File

class BMDBRegistrationForm(
    context: Context,
    options: Map<DatabaseOption<*>, Any>
) : RegistrationForm<BMDBMeta>(context, options) {

    private val databaseName: StringProperty = SimpleStringProperty()
    private val databaseDir: StringProperty = SimpleStringProperty()
    private val authentication: BooleanProperty = SimpleBooleanProperty()
    private val username: StringProperty = SimpleStringProperty()
    private val password: StringProperty = SimpleStringProperty()
    private val passwordRepeat: StringProperty = SimpleStringProperty()
    private val fullPath: StringProperty =
        databaseDir.concat(File.separator)
            .concat(databaseName)
            .concat(".")
            .concat(System.getProperty("boomega.file.extension"))
            .asStringProperty()

    private val databaseFile get() = File(fullPath.get())
    private val databaseDirFile: File get() = File(databaseDir.get())

    private val generalValidator = Validator()
    private val credentialsValidator = Validator()

    override val persistable: ObservableBooleanValue
        get() = generalValidator.containsErrorsProperty()
            .or(credentialsValidator.containsErrorsProperty().and(authentication))
            .not()

    override val node: Node = Grid()

    override fun registrate(): BMDBMeta {
        databaseDirFile.mkdirs()
        val meta = BMDBMeta(databaseName.get(), databaseFile)
        val credentials = mapOf<DatabaseField<*>, Any>(
            BMDBProvider.USERNAME_FIELD to username.get(),
            BMDBProvider.PASSWORD_FIELD to password.get()
        )
        BMDBProvider.getDatabase(meta, credentials, emptyMap()).close() // TODO: database options
        return meta
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
            buildPasswordInputs()
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
            validateImmediate(generalValidator) { it, _ ->
                when {
                    databaseName.get().isEmpty() -> it.error(i18n("database.creator.missing_name.title"))
                    databaseFile.exists() -> it.error(i18n("database.creator.file_already_exists.title"))
                }
            }
        }

        private fun buildDirField() = TextField().apply {
            setConstraints(this, 1, 1)
            setHgrow(this, Priority.ALWAYS)
            minHeight = 35.0
            text = System.getProperty("boomega.dir.default.path");
            databaseDir.bindBidirectional(textProperty())
            textProperty().bindBidirectional(databaseDir)
            validateImmediate(generalValidator) { it, _ ->
                when {
                    databaseDirFile.hasValidPath.not() -> it.error(i18n("database.creator.invalid_dir.title"))
                    databaseDir.get().isBlank() -> it.error(i18n("database.creator.missing_dir.title"))
                    databaseDirFile.exists().not() -> it.warn(
                        i18n(
                            "database.creator.dir_not_exist.title",
                            databaseDirFile.name
                        )
                    )
                }
            }
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
            buildUsernameValidation(this)
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

        private fun buildPasswordInputs() {
            val passwordInput = buildPasswordInput()
            val repeatPasswordInput = buildRepeatPasswordInput()
            children.add(passwordInput)
            children.add(repeatPasswordInput)
            buildPasswordValidation(passwordInput, repeatPasswordInput)
        }

        private fun buildPasswordValidation(passwordField: TextField, repeatField: TextField) {
            credentialsValidator.createCheck()
                .dependsOn("pswd", passwordField.textProperty())
                .dependsOn("rpswd", repeatField.textProperty())
                .decorates(passwordField)
                .decorates(repeatField)
                .withMethod {

                    val pswd: String = it["pswd"]
                    val rpswd: String = it["rpswd"]
                    when {
                        pswd != rpswd ->
                            it.error(i18n("database.creator.passwords_not_match.title"))
                        pswd.isBlank() || rpswd.isBlank() ->
                            it.error(i18n("database.creator.empty_password.title"))
                    }
                }
                .immediate()
        }

        private fun buildUsernameValidation(usernameField: TextField) {
            credentialsValidator.createCheck()
                .dependsOn("usrname", usernameField.textProperty())
                .decorates(usernameField)
                .withMethod {
                    val value: String = it["usrname"]
                    when {
                        value.isBlank() ->
                            it.error(i18n("database.creator.empty_user_name.title"))
                    }
                }
                .immediate()
        }

        private fun openDirectory() {
            directoryChooser.showDialog(this.window)?.let { dir ->
                databaseDir.set(dir.absolutePath)
            }
        }
    }
}