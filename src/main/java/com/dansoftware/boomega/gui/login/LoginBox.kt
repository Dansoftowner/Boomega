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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.config.logindata.LoginData
import com.dansoftware.boomega.database.api.DatabaseConstructionException
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.LoginForm
import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.dbcreator.BMDBDatabaseOpener
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.refresh
import com.dansoftware.boomega.gui.util.selectedItem
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
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
    private val databaseTracker: DatabaseTracker,
    private val databaseLoginListener: DatabaseLoginListener
) : VBox(10.0) {

    private val itemSelected: BooleanProperty = SimpleBooleanProperty()
    private val remember: BooleanProperty = SimpleBooleanProperty()

    private val loginForm: ObjectProperty<LoginForm<*>?> = SimpleObjectProperty()

    private val databaseChooser: DatabaseCombo = buildDatabaseChooser()

    private var selectedItem: DatabaseMeta?
        get() = databaseChooser.selectedItem
        set(value) {
            databaseChooser.selectionModel.select(value)
        }

    init {
        this.styleClass.add("login-box")
        this.maxWidth = 650.0
        this.prefWidth = 550.0
        this.buildUI()
    }

    fun titleProperty(): ObservableStringValue = databaseChooser.selectedItemProperty().asString()

    fun refresh() {
        databaseChooser.refresh()
    }

    fun fillForm(loginData: LoginData) {
        databaseChooser.items.addAll(loginData.savedDatabases)
        loginData.selectedDatabase?.let(databaseChooser.selectionModel::select)

        if (loginData.isAutoLogin) {
            loginData.autoLoginCredentials!!.run {

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
        children.add(buildDatabaseChooser())
        children.add(buildFileChooserButton())
        children.add(buildDatabaseManagerButton())
        setMargin(this, Insets(0.0, 20.0, 0.0, 20.0))
    }

    private fun buildDatabaseChooser() = DatabaseCombo(databaseTracker).apply {
        loginForm.bind(
            Bindings.createObjectBinding({
                @Suppress("UNCHECKED_CAST")
                selectedItem?.provider?.buildUILoginForm(
                    context,
                    selectedItemProperty() as ObjectProperty<DatabaseMeta>,
                    emptyMap() // TODO: database options
                )
            })
        )
        itemSelected.bind(selectionModel.selectedItemProperty().isNotNull)
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
            DatabaseCreatorActivity().show(databaseTracker, context.contextWindow)
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
            children[1] = newForm
        }
    }


    private fun buildCheckBox() = CheckBox().apply {
        alignment = Pos.CENTER_RIGHT
        text = i18n("login.form.remember")
        remember.bindBidirectional(selectedProperty())
        selectedProperty().bindBidirectional(remember)
    }

    private fun buildLoginButton() = Button().apply {
        minHeight = 35.0
        maxWidth = Double.MAX_VALUE
        text = i18n("login.form.login")
        isDefaultButton = true
        setOnAction {
            // TODO: what about already launched databases? Should send front request to database activity
            try {
                databaseLoginListener.onDatabaseOpened(loginForm.get()!!.login())
            } catch (e: DatabaseConstructionException) {
                context.showErrorDialog(i18n("login.failed"), e.localizedMessage ?: "", e)
            }
        }
    }


    @Deprecated("BS")
    interface Controller {
        var loginBox: LoginBox?

        val context: Context
        val preferences: Preferences
        val databaseTracker: DatabaseTracker
        val loginData: LoginData

        fun openDatabaseManager()
        fun openFile()
        fun openDatabaseCreator()
        fun login(databaseMeta: DatabaseMeta, credentials: Credentials, remember: Boolean)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoginBox::class.java)
    }
}
