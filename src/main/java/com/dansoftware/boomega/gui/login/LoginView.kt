package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.config.logindata.LoginData
import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.db.NitriteDatabase
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.ContextTransformable
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbcreator.DatabaseOpener
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.info.InformationActivity
import com.dansoftware.boomega.gui.mainview.MainActivity
import com.dansoftware.boomega.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.boomega.gui.preferences.PreferencesActivity
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.runOnUiThread
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.dlsc.workbenchfx.SimpleHeaderView
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableStringValue
import javafx.concurrent.Task
import javafx.scene.control.MenuItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the [Database] object.
 *
 * @author Daniel Gyorffy
 */
class LoginView(
    private val preferences: Preferences,
    tracker: DatabaseTracker,
    loginData: LoginData,
    databaseLoginListener: DatabaseLoginListener
) : SimpleHeaderView<LoginViewBase>(
    I18N.getValue("database.auth"),
    MaterialDesignIconView(MaterialDesignIcon.LOGIN)
), ContextTransformable {

    private val asContext: Context = Context.from(this)
    private val loginBoxController = LoginBoxController(context, tracker, loginData, preferences, databaseLoginListener)
    private val createdDatabase: ObjectProperty<Database> = SimpleObjectProperty()

    init {
        content = LoginViewBase(loginBoxController)
        buildToolbar()
    }

    private fun buildToolbar() {
        toolbarControlsRight.add(buildOptionsItem())
        toolbarControlsRight.add(buildInfoItem())
    }

    val loginData: LoginData
        get() = loginBoxController.loginData

    fun createdDatabaseProperty(): ReadOnlyObjectProperty<Database> {
        return createdDatabase
    }

    override fun getContext(): Context {
        return asContext
    }

    fun titleProperty(): ObservableStringValue {
        return loginBoxController.titleProperty()!!
    }

    private fun buildInfoItem(): ToolbarItem = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.INFORMATION)) {
        InformationActivity(context).show()
    }

    private fun buildOptionsItem() = ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.SETTINGS)).apply {
        items.addAll(
            MenuItem(I18N.getValue("update.search"), MaterialDesignIconView(MaterialDesignIcon.UPDATE)).action {
                val task = object : Task<UpdateSearcher.UpdateSearchResult>() {
                    init {
                        setOnSucceeded {
                            context.stopProgress()
                            UpdateActivity(context, it.source.value as UpdateSearcher.UpdateSearchResult).show()
                        }
                        setOnRunning { context.showIndeterminateProgress() }
                    }

                    override fun call() = UpdateSearcher.defaultInstance().search()
                }
                CachedExecutor.submit(task)
            },

            MenuItem(I18N.getValue("plugin.manager.open"), FontAwesomeIconView(FontAwesomeIcon.PLUG)).action {
                PluginManagerActivity().show(context.contextWindow)
            },

            MenuItem(I18N.getValue("app.settings"), MaterialDesignIconView(MaterialDesignIcon.SETTINGS)).action {
                PreferencesActivity(preferences).show(context.contextWindow)
            }
        )
    }

    private class LoginBoxController(
        override val context: Context,
        override val databaseTracker: DatabaseTracker,
        override val loginData: LoginData,
        private val preferences: Preferences,
        private val databaseLoginListener: DatabaseLoginListener
    ) : LoginBox.Controller, DatabaseTracker.Observer {

        init {
            databaseTracker.registerObserver(this)
        }

        override var loginBox: LoginBox? = null
            set(value) {
                value?.takeIf { field !== it }?.let(this::initLoginBox)
                field = value
            }

        fun titleProperty() = loginBox?.titleProperty()

        private fun initLoginBox(box: LoginBox) {
            box.fillForm(loginData)
            box.addSelectedItemListener {
                loginData.selectedDatabase = it
            }
        }

        override fun openDatabaseManager() {
            DatabaseManagerActivity().show(databaseTracker, context.contextWindow)
        }

        override fun openFile() {
            DatabaseOpener().showMultipleOpenDialog(context.contextWindow).stream()
                .peek(databaseTracker::addDatabase)
                .reduce { _, second -> second }
                .ifPresent { loginBox?.select(it) }
        }

        override fun openDatabaseCreator() {
            DatabaseCreatorActivity().show(databaseTracker, context.contextWindow).ifPresent {
                loginBox?.select(it)
            }
        }

        override fun login(databaseMeta: DatabaseMeta, credentials: Credentials, remember: Boolean) {
            when {
                databaseTracker.isDatabaseUsed(databaseMeta) ->
                    MainActivity.getByDatabase(databaseMeta)
                        .map(MainActivity::getContext)
                        .ifPresent(Context::toFront)
                else -> {
                    loginData.isAutoLogin = remember
                    loginData.autoLoginCredentials = credentials.takeIf { remember }

                    NitriteDatabase.getAuthenticator()
                        .onFailed { title, message, t ->
                            context.showErrorDialog(title, message, t as Exception?)
                            logger.error("Failed to create/open the database", t)
                        }.auth(databaseMeta, credentials)?.let {
                            logger.debug("Signing in was successful; closing the LoginWindow")
                            preferences.editor().put(PreferenceKey.LOGIN_DATA, loginData)
                            databaseLoginListener.onDatabaseOpened(it)
                            context.close()
                        }
                }
            }
        }

        override fun onUsingDatabase(databaseMeta: DatabaseMeta) {
            runOnUiThread {
                loginBox?.refresh()
                when (databaseMeta) {
                    loginBox?.selectedItem -> {
                        loginBox?.select(null)
                    }
                }
            }
        }

        override fun onClosingDatabase(databaseMeta: DatabaseMeta) {
            runOnUiThread { loginBox?.refresh() }
        }

        override fun onDatabaseAdded(databaseMeta: DatabaseMeta) {
            runOnUiThread {
                logger.debug("Adding database {}", databaseMeta)
                loginBox?.addItem(databaseMeta)
                loginData.savedDatabases.add(databaseMeta)
            }
        }

        override fun onDatabaseRemoved(databaseMeta: DatabaseMeta) {
            runOnUiThread {
                loginBox?.removeItem(databaseMeta)
                loginData.savedDatabases.remove(databaseMeta)
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoginView::class.java)
    }
}