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
import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.database.bmdb.BMDBDatabaseOpener
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.login.config.LoginData
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import javafx.application.Platform.runLater
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableStringValue
import javafx.concurrent.Task
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
import java.util.concurrent.TimeUnit

class LoginBox(
    private val context: Context,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker,
    private val databaseLoginListener: DatabaseLoginListener
) : VBox(10.0), DatabaseTracker.Observer {

    private val loginFormCache: Cache<DatabaseProvider<*>, LoginForm<*>> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10)
            .build()

    private val loginIsInProcess: BooleanProperty = SimpleBooleanProperty()
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
                loginFormCache.get(selectedItem?.provider) {
                    @Suppress("UNCHECKED_CAST")
                    selectedItem?.provider?.buildUILoginForm(
                        context,
                        selectedItemProperty() as ReadOnlyObjectProperty<DatabaseMeta>,
                        emptyMap() // TODO: database options
                    )
                }
            }, selectionModel.selectedItemProperty())
        )
        itemSelected.bind(selectionModel.selectedItemProperty().isNotNull)
    }

    private fun buildHeader() = StackPane().apply {
        styleClass.add("header")
        padding = Insets(20.0)
        children.add(
            HBox(10.0).run {
                children.add(ImageView().apply { styleClass.add("logo") })
                children.add(StackPane(Label(System.getProperty("app.name"))))
                Group(this)
            }
        )
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

        val baseSize = children.size
        loginForm.addListener { _, _, newForm ->
            logger.debug("New Login form")

            if (baseSize < children.size)
                children.removeAt(1)

            // For some reason, an NPE might appear we don't care about
            try {
                children.add(1, newForm!!.node)
            } catch (ignored: NullPointerException) {
            }
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
        disableProperty().bind(loginIsInProcess)
        setOnAction { loginRequest() }
    }

    private fun loginRequest() {
        when {
            databaseTracker.isDatabaseUsed(selectedDatabase) ->
                databaseLoginListener.onUsedDatabaseOpened(selectedDatabase!!)
            else -> {
                login { database ->
                    preferences.updateLoginData { it.isAutoLogin = remember.get() }
                    databaseLoginListener.onDatabaseOpened(database)
                    preferences.updateLoginData { it.isAutoLogin = remember.get() }
                    context.close()
                }
            }
        }
    }

    private inline fun login(crossinline onDatabaseCreated: (Database) -> Unit) {
        CachedExecutor.submit(
            object : Task<Database>() {
                init {
                    loginIsInProcess.bind(runningProperty())
                    onRunning {
                        context.showIndeterminateProgress()
                    }
                    onFailed {
                        context.stopProgress()
                        when (it) {
                            is DatabaseConstructionException ->
                                context.showErrorDialog(i18n("login.failed"), it.localizedMessage ?: "", it)
                            is Exception ->
                                // TODO: message
                                context.showErrorDialog(i18n("login.failed"), message = "", it)
                        }

                    }
                    onSucceeded {
                        context.stopProgress()
                        onDatabaseCreated(value)
                    }
                }

                override fun call(): Database {
                    return loginForm.get()!!.login()
                }
            }
        )
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
