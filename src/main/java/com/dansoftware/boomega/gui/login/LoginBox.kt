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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseConstructionException
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.LoginForm
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.database.bmdb.BMDBDatabaseOpener
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.login.config.LoginData
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.refresh
import com.dansoftware.boomega.gui.util.selectedItem
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.i18n.i18n
import javafx.application.Platform.runLater
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableStringValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoginBox(
    private val context: Context,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker,
    private val databaseLoginListener: DatabaseLoginListener
) : VBox(10.0), DatabaseTracker.Observer {

    private val itemSelected: BooleanProperty = SimpleBooleanProperty()
    private val remember: BooleanProperty = SimpleBooleanProperty()
    private val loginForm: ObjectProperty<LoginForm<*>?> = SimpleObjectProperty()
    private val databaseChooser: DatabaseCombo = buildDatabaseChooser()

    var selectedDatabase: DatabaseMeta?
        get() = databaseChooser.selectedItem
        set(value) {
            databaseChooser.selectionModel.select(value)
            databaseChooser.refresh()
        }

    init {
        // for tracking the databases
        databaseTracker.registerObserver(this)
        styleClass.add("login-box")
        maxWidth = 650.0
        prefWidth = 550.0
        buildUI()
        fillForm(preferences[LOGIN_DATA])
    }

    fun titleProperty(): ObservableStringValue = databaseChooser.selectedItemProperty().asString()

    fun refresh() {
        databaseChooser.refresh()
    }

    private fun fillForm(loginData: LoginData) {
        databaseChooser.items.addAll(loginData.savedDatabases)
        loginData.selectedDatabase?.let(databaseChooser.selectionModel::select)

        if (loginData.isAutoLogin) {
            loginData.autoLoginCredentials!!.run {
                // TODO
            }
        }

        /*loginData.autoLoginCredentials?.run {

        }
        loginData.autoLoginDatabase?.let {
            remember.set(true)
            loginData.autoLoginCredentials?.run {
                usernameInput.set(username)
                passwordInput.set(password)
            }
        }*/

    }

    private fun buildUI() {
        children.add(buildHeader())
        children.add(buildDatabaseChooserArea())
        children.add(buildForm())
        children.add(Separator())
        children.add(buildDataSourceButton())
    }

    private fun buildDatabaseChooser() = DatabaseCombo(preferences, databaseTracker).apply {
        loginForm.bind(
            Bindings.createObjectBinding({
                @Suppress("UNCHECKED_CAST")
                selectedItem?.provider?.buildUILoginForm(
                    context,
                    selectedItemProperty() as ReadOnlyObjectProperty<DatabaseMeta>,
                    emptyMap() // TODO: database options
                )
            }, selectionModel.selectedItemProperty())
        )
        itemSelected.bind(selectionModel.selectedItemProperty().isNotNull)
    }

    private fun buildHeader() = StackPane().apply {
        styleClass.add("header")
        padding = Insets(20.0)
        HBox(10.0).run {
            children.add(ImageView().apply { styleClass.add("logo") })
            children.add(StackPane(Label(System.getProperty("app.name"))))
            Group(this)
        }.let(children::add)
    }

    private fun buildDatabaseChooserArea() = HBox(5.0).apply {
        children.add(databaseChooser)
        children.add(buildFileChooserButton())
        children.add(buildDatabaseManagerButton())
        setMargin(this, Insets(0.0, 20.0, 0.0, 20.0))
    }


    private fun buildFileChooserButton() = Button().apply {
        tooltip = Tooltip(i18n("login.source.open"))
        graphic = icon("folder-open-icon")
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        minHeight = 35.0
        minWidth = 40.0
        setOnAction {
            BMDBDatabaseOpener().showOpenDialog(context.contextWindow)?.let {
                databaseTracker.saveDatabase(it)
            }
        }
    }

    private fun buildDatabaseManagerButton() = Button().apply {
        tooltip = Tooltip(i18n("login.db.manager.open"))
        graphic = icon("database-icon")
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        minHeight = 35.0
        minWidth = 40.0
        setOnAction {
            DatabaseManagerActivity().show(databaseTracker, context.contextWindow)
        }
    }

    private fun buildDataSourceButton() = Button().apply {
        minHeight = 35.0
        styleClass.add("source-adder")
        text = i18n("login.add.source")
        maxWidth = Double.MAX_VALUE
        graphic = icon("database-plus-icon")
        setOnAction {
            DatabaseCreatorActivity().show(databaseTracker, context.contextWindow)?.let {
                selectedDatabase = it
            }
        }
    }

    private fun buildForm() = VBox(10.0).apply {
        setMargin(this, Insets(0.0, 20.0, 20.0, 20.0))
        managedProperty().bind(itemSelected)
        visibleProperty().bind(itemSelected)

        children.add(Separator())
        children.add(buildCheckBox())
        children.add(buildLoginButton())

        loginForm.addListener { _, _, newForm ->
            logger.debug("New Login form")

            if (children[1] is LoginForm<*>)
                children.removeAt(1)
            children.add(1, newForm)
        }
    }

    private fun buildCheckBox() = CheckBox().apply {
        alignment = Pos.CENTER_RIGHT
        text = i18n("login.form.remember")
        Bindings.bindBidirectional(selectedProperty(), remember)
    }

    private fun buildLoginButton() = Button().apply {
        minHeight = 35.0
        maxWidth = Double.MAX_VALUE
        text = i18n("login.form.login")
        isDefaultButton = true
        setOnAction {
            try {
                when {
                    databaseTracker.isDatabaseUsed(selectedDatabase) ->
                        databaseLoginListener.onUsedDatabaseOpened(selectedDatabase!!)
                    else -> {
                        preferences.updateLoginData { it.isAutoLogin = remember.get() }
                        databaseLoginListener.onDatabaseOpened(loginForm.get()!!.login())
                        preferences.updateLoginData { it.isAutoLogin = remember.get() }
                        context.close()
                    }
                }

            } catch (e: DatabaseConstructionException) {
                logger.debug("Couldn't construct database", e)
                context.showErrorDialog(i18n("login.failed"), e.localizedMessage ?: "", e)
            }
        }
    }

    override fun onUsingDatabase(databaseMeta: DatabaseMeta) {
        runLater(::refresh)
    }

    override fun onClosingDatabase(databaseMeta: DatabaseMeta) {
        runLater(::refresh)
    }

    override fun onDatabaseAdded(databaseMeta: DatabaseMeta) {
        runLater {
            databaseChooser.items.add(databaseMeta)
        }
    }

    override fun onDatabaseRemoved(databaseMeta: DatabaseMeta) {
        runLater {
            databaseChooser.items.remove(databaseMeta)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoginBox::class.java)
    }
}
