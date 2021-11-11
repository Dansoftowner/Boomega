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

package com.dansoftware.boomega.gui.dbcreator

import com.dansoftware.boomega.database.api.DatabaseConstructionException
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.database.api.RegistrationForm
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.padding
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane


class DatabaseCreatorForm(
    private val context: Context,
    private val databaseTracker: DatabaseTracker,
    private val databaseType: ObjectProperty<DatabaseProvider<*>>
) : BorderPane() {

    private val registrationForm: ObjectProperty<RegistrationForm<*>?> =
        SimpleObjectProperty<RegistrationForm<*>?>().apply {
            bind(
                Bindings.createObjectBinding(
                    // TODO: database options
                    { databaseType.get()?.buildUIRegistrationForm(context, emptyMap()) },
                    databaseType
                )
            )
        }

    var createdDatabase: DatabaseMeta? = null
        private set

    init {
        buildUI()
    }

    private fun buildUI() {
        center = buildCenter()
        bottom = buildBottom()
    }

    private fun buildCenter() = ScrollPane().apply {
        isFitToWidth = true
        contentProperty().bind(registrationForm)
    }

    private fun buildBottom() = Button().run {
        maxWidth = Double.MAX_VALUE
        minHeight = 35.0
        text = i18n("database.creator.create")
        isDefaultButton = true
        setOnAction {
            constructDatabase()?.let {
                databaseTracker.saveDatabase(it)
                createdDatabase = it
                context.close()
            }
        }
        StackPane(this).padding(Insets(10.0))
    }

    private fun constructDatabase() =
        try {
            registrationForm.get()!!.registrate()
        } catch (e: DatabaseConstructionException) {
            context.showErrorDialog(
                title = e.title ?: i18n("database.create_failed"),
                message = e.localizedMessage ?: ""
            )
            null
        }
}


/*
class DatabaseCreatorForm(
    private val context: Context,
    private val databaseTracker: DatabaseTracker,
    private val databaseType: ObjectProperty<DatabaseProvider<*>>
) : BorderPane() {

    private val createdDatabase: ObjectProperty<DatabaseMeta> = SimpleObjectProperty()

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

    private val directoryChooser = DirectoryChooser()

    init {
        buildUI()
    }

    fun createdDatabaseProperty() = createdDatabase

    private fun buildUI() {
        center = buildCenter()
        bottom = buildBottom()
    }

    private fun buildCenter() = ScrollPane(buildGrid()).apply {
        isFitToWidth = true
    }

    private fun buildBottom() = StackPane(buildCreateButton()).apply {
        padding = Insets(10.0)
    }

    private fun openDirectory() {
        directoryChooser.showDialog(this.window)?.let { dir ->
            databaseDir.set(dir.absolutePath)
        }
    }

    private fun buildCreateButton() = Button().apply {
        maxWidth = Double.MAX_VALUE
        minHeight = 35.0
        text = i18n("database.creator.create")
        isDefaultButton = true
        setOnAction { create() }
    }

    private fun buildGrid() = object : GridPane() {

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
    }


    private fun create() {
        validateInputs { databaseMeta, credentials ->
            createdDatabase.set(databaseMeta)
          */
/*  NitriteDatabase.builder()
                .databaseMeta(databaseMeta)
                .onFailed { message, t ->
                    createdDatabase.set(null)
                    context.showErrorDialog(i18n("database.create_failed"), message, t as Exception?) {}
                }.touch(credentials)*//*

            databaseTracker.saveDatabase(databaseMeta)
            this.stage?.close()
        }
    }

    private fun validateInputs(onSuccess: (DatabaseMeta, Credentials) -> Unit) {
        val databaseFile = fullPath.get().let(::File)
        val databaseDirFile = databaseDir.get().let(::File)
        when {
            databaseName.get()?.isBlank() ?: true -> {
                showErrorDialog(
                    "database.creator.missing_name.title",
                    "database.creator.missing_name.msg"
                )
                false
            }
            databaseDir.get()?.isBlank() ?: true -> {
                showErrorDialog(
                    "database.creator.missing_dir.title",
                    "database.creator.missing_dir.msg"
                )
                false
            }
            databaseDirFile.hasValidPath.not() -> {
                showErrorDialog(
                    "database.creator.invalid_dir.title",
                    "database.creator.invalid_dir.msg",
                    databaseDirFile
                )
                false
            }
            databaseFile.exists() -> {
                showErrorDialog(
                    "database.creator.file_already_exists.title",
                    "database.creator.file_already_exists.msg",
                    databaseFile.shortenedPath(1)
                )
                false
            }
            authentication.get() && username.get()?.isBlank() ?: true -> {
                showErrorDialog(
                    "database.creator.empty_user_name.title",
                    "database.creator.empty_user_name.msg"
                )
                false
            }
            authentication.get() && password.get()?.isBlank() ?: true -> {
                showErrorDialog(
                    "database.creator.empty_password.title",
                    "database.creator.empty_password.msg"
                )
                false
            }
            authentication.get() && password.get() != passwordRepeat.get() -> {
                showErrorDialog(
                    "database.creator.passwords_not_match.title",
                    "database.creator.passwords_not_match.msg"
                )
                false
            }
            databaseDirFile.exists().not() -> {
                showInfoDialog(
                    "database.creator.dir_not_exist.title",
                    "database.creator.dir_not_exist.msg",
                    databaseDirFile.name
                )
                try {
                    databaseDirFile.mkdirs();
                } catch (e: java.lang.SecurityException) {
                    //TODO: handle
                }
                true
            }
            else -> true
        }.takeIf { it }?.let {
            onSuccess(
                TODO()*/
/*DatabaseMeta(databaseName.get(), databaseFile)*//*
,
                Credentials(username.get(), password.get())
            )
        }
    }

    private fun showInfoDialog(@Nls title: String, @Nls msg: String, vararg args: Any?): ButtonType? =
        context.showInformationDialogAndWait(i18n(title, *args), i18n(msg))

    private fun showErrorDialog(@Nls title: String, @Nls msg: String, vararg args: Any?): ButtonType? =
        context.showErrorDialogAndWait(i18n(title), i18n(msg, *args))

}*/
