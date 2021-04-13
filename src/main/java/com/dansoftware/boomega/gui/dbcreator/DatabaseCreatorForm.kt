package com.dansoftware.boomega.gui.dbcreator

import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.db.NitriteDatabase
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.SpaceValidator
import com.dansoftware.boomega.gui.util.WindowUtils
import com.dansoftware.boomega.i18n.I18N
import com.jfilegoodies.FileGoodies
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.*
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.DirectoryChooser
import org.apache.commons.lang3.StringUtils
import java.io.File

class DatabaseCreatorForm(
    private val context: Context,
    private val databaseTracker: DatabaseTracker
) : BorderPane() {

    private val createdDatabase: ObjectProperty<DatabaseMeta> = SimpleObjectProperty()

    private val databaseName: StringProperty = SimpleStringProperty()
    private val databaseDir: StringProperty = SimpleStringProperty()
    private val authentication: BooleanProperty = SimpleBooleanProperty()
    private val username: StringProperty = SimpleStringProperty()
    private val password: StringProperty = SimpleStringProperty()
    private val passwordRepeat: StringProperty = SimpleStringProperty()

    private val fullPath: StringProperty = SimpleStringProperty().apply {
        bind(databaseDir.concat(File.separator)
            .concat(databaseName)
            .concat(".")
            .concat(System.getProperty("libraryapp.file.extension")))
    }

    private val directoryChooser = DirectoryChooser()

    init {
        buildUI()
    }

    fun createdDatabaseProperty() = createdDatabase

    private fun buildUI() {
        center = ScrollPane(buildGrid()).apply { isFitToWidth = true }
        bottom = buildCreateButton()
    }

    private fun openDirectory() {
        directoryChooser.showDialog(WindowUtils.getWindowOf(this))?.let { dir ->
            databaseDir.set(dir.absolutePath)
        }
    }

    private fun buildCreateButton() = Button().apply {
        maxWidth = Double.MAX_VALUE
        minHeight = 35.0
        text = I18N.getValue("data.source.adder.create")
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
            children.add(buildLabel("data.source.adder.db.name", 0, 0))
            children.add(buildNameField())
            children.add(buildLabel("data.source.adder.db.dir", 1, 0))
            children.add(buildDirField())
            children.add(buildDirOpenButton())
            children.add(buildLabel("data.source.adder.db.full.path", 0, 2))
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

        private fun buildLabel(i18n: String, column: Int, row: Int) = Label(I18N.getValue(i18n)).apply {
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
            text = System.getProperty("libraryapp.dir.default.path");
            databaseDir.bindBidirectional(textProperty())
            textProperty().bindBidirectional(databaseDir)
        }

        private fun buildDirOpenButton() = Button().apply {
            setConstraints(this, 2, 1)
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN)
            minHeight = 35.0
            setOnAction { openDirectory() }
        }

        private fun buildFullPathField() = TextField().apply {
            setConstraints(this, 0, 3)
            setColumnSpan(this, 3)
            minHeight = 35.0
            textProperty().bind(fullPath)
        }

        private fun buildAuthenticationCheck() = CheckBox(I18N.getValue("data.source.adder.db.auth")).apply {
            setConstraints(this, 0, 5)
            setColumnSpan(this, 3)
            setHgrow(this, Priority.SOMETIMES)
            isSelected = true
            authentication.bind(selectedProperty())
        }

        private fun buildUsernameInput() = TextField().apply {
            setConstraints(this, 0, 6)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = I18N.getValue("login.form.username.prompt")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            username.bind(textProperty())
        }

        private fun buildPasswordInput() = PasswordField().apply {
            setConstraints(this, 0, 7)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = I18N.getValue("login.form.password.prompt")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            password.bind(textProperty())
        }

        private fun buildRepeatPasswordInput() = PasswordField().apply {
            setConstraints(this, 0, 8)
            setColumnSpan(this, 3)
            minHeight = 35.0
            promptText = I18N.getValue("login.form.password_repeat.prompt")
            textFormatter = SpaceValidator()
            visibleProperty().bind(authentication)
            managedProperty().bind(authentication)
            passwordRepeat.bind(textProperty())
        }
    }


    private fun create() {
        validateInputs { databaseMeta, credentials ->
            createdDatabase.set(databaseMeta)
            NitriteDatabase.getAuthenticator()
                .onFailed { title, message, t ->
                    createdDatabase.set(null)
                    context.showErrorDialog(title, message, t as Exception?) {}
                }.touch(databaseMeta, credentials)
            databaseTracker.addDatabase(databaseMeta)
            WindowUtils.getStageOf(this)?.close()
        }
    }

    private fun validateInputs(onSuccess: (DatabaseMeta, Credentials) -> Unit) {
        val databaseFile = fullPath.get().let(::File)
        val databaseDirFile = databaseDir.get().let(::File)
        when {
            StringUtils.isBlank(databaseName.get()) -> {
                showErrorDialog(
                    "db.creator.form.invalid.missing.name.title",
                    "db.creator.form.invalid.missing.name.msg"
                )
                false
            }
            StringUtils.isBlank(databaseDir.get()) -> {
                showErrorDialog(
                    "db.creator.form.invalid.missing.dir.title",
                    "db.creator.form.invalid.missing.dir.msg"
                )
                false
            }
            FileGoodies.hasNotValidPath(databaseDirFile) -> {
                showErrorDialog(
                    "db.creator.form.invalid.dir.title",
                    "db.creator.form.invalid.dir.msg",
                    databaseDirFile
                )
                false
            }
            databaseFile.exists() -> {
                showErrorDialog(
                    "db.creater.form.invalid.file.already.exists.title",
                    "db.creater.form.invalid.file.already.exists.msg",
                    FileGoodies.shortenedFilePath(databaseFile, 1)
                )
                false
            }
            authentication.get() && StringUtils.isBlank(username.get()) -> {
                showErrorDialog(
                    "db.creator.form.invalid.empty.user.name.title",
                    "db.creator.form.invalid.empty.user.name.msg"
                )
                false
            }
            authentication.get() && StringUtils.isBlank(password.get()) -> {
                showErrorDialog(
                    "db.creator.form.invalid.empty.password.title",
                    "db.creator.form.invalid.empty.password.msg"
                )
                false
            }
            authentication.get() && password.get().equals(passwordRepeat.get()).not() -> {
                showErrorDialog(
                    "db.creator.form.invalid.password_match.title",
                    "db.creator.form.invalid.password_match.msg"
                )
                false
            }
            databaseDirFile.exists().not() -> {
                showInfoDialog(
                    "db.creator.form.confirm.dir.not.exist.title",
                    "db.creator.form.confirm.dir.not.exist.msg",
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
                DatabaseMeta(databaseName.get(), databaseFile),
                Credentials(username.get(), password.get())
            )
        }
    }

    private fun showInfoDialog(title: String, msg: String, vararg args: Any?): ButtonType? =
        context.showInformationDialogAndWait(I18N.getValue(title, *args), I18N.getValue(msg))

    private fun showErrorDialog(title: String, msg: String, vararg args: Any?): ButtonType? =
        context.showErrorDialogAndWait(I18N.getValue(title), I18N.getValue(msg, *args))

}