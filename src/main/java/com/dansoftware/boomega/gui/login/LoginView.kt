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
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.gui.base.BaseView
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.onScenePresent
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableStringValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A LoginView is a graphical object that can handle
 * a login request and creates the [Database] object.
 *
 * @author Daniel Gyorffy
 */
class LoginView(
    preferences: Preferences,
    tracker: DatabaseTracker,
    loginData: LoginData,
    databaseLoginListener: DatabaseLoginListener
) : BaseView() {

    private val createdDatabase: ObjectProperty<Database> = SimpleObjectProperty()

    val loginData: LoginData
        get() = TODO()

    init {
        content = LoginViewBase(TODO())
        playAnimation()
    }

    private fun playAnimation() {
        onScenePresent {
            Platform.runLater {
                animatefx.animation.FadeIn(this).play()
            }
        }
    }

    fun createdDatabaseProperty(): ReadOnlyObjectProperty<Database> {
        return createdDatabase
    }

    fun titleProperty(): ObservableStringValue {
       TODO()
    }
/*
    private class LoginBoxController(
        override val context: Context,
        override val databaseTracker: DatabaseTracker,
        override val loginData: LoginData,
        override val preferences: Preferences,
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
                .peek(databaseTracker::saveDatabase)
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
                    DatabaseActivity.getByDatabase(databaseMeta)
                        .map(DatabaseActivity::context)
                        .ifPresent(Context::toFrontRequest)
                else -> {
                    loginData.isAutoLogin = remember
                    loginData.autoLoginCredentials = credentials.takeIf { remember }

                    NitriteDatabase.builder()
                        .databaseMeta(databaseMeta)
                        .onFailed { message, t ->
                            context.showErrorDialog(i18n("login.failed"), message, t as Exception?)
                            logger.error("Failed to create/open the database", t)
                        }.build(credentials)?.let {
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
*/
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoginView::class.java)
    }
}