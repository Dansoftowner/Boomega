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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.gui.action.AvailableActions
import com.dansoftware.boomega.gui.menu.getPreferredGeneralMenuBar
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import org.slf4j.LoggerFactory
import java.util.*

/**
 * A LoginWindow is a javaFX [javafx.stage.Stage] that should be
 * used to display [LoginView] gui-objects.
 *
 *
 *
 * Also, when a user closes the LoginWindow, it will save the [com.dansoftware.boomega.config.logindata.LoginData] to the
 * configurations.
 */
private class LoginWindow(
    private val root: LoginView
) : BaseWindow<LoginView>(
    TitleProperty("window.login.title", " - ", root.titleProperty()),
    getPreferredGeneralMenuBar(root),
    root
) {

    init {
        this.exitDialog = true
        this.isMaximized = true
        this.minWidth = 530.0
        this.minHeight = 530.0
        initKeyBindings()
    }

    private fun initKeyBindings() {
        AvailableActions.applyOnScene(scene, root)
    }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(LoginWindow::class.java)
    }

    /**
     * For building the login-window's title-property
     */
    private class TitleProperty(i18n: String, separator: String, changingString: ObservableStringValue) :
        SimpleStringProperty() {

        init {
            val baseTitle = SimpleStringProperty(I18N.getValues().getString(i18n))
            val separatorAndChangingObservable = buildSeparatorAndChangingObservable(separator, changingString)
            this.bind(baseTitle.concat(separatorAndChangingObservable))
        }

        private fun buildSeparatorAndChangingObservable(
            separator: String,
            changingString: ObservableStringValue
        ): ObservableStringValue =
            object : SimpleStringProperty(), ChangeListener<String> {
                init {
                    copyValue(separator, changingString.value)
                    changingString.addListener(this)
                }

                private fun copyValue(separator: String, newValue: String?) {
                    when (newValue) {
                        null, "null" -> this.set("")
                        else -> this.set(separator.plus(newValue))
                    }
                }

                override fun changed(observable: ObservableValue<out String>, oldValue: String, newValue: String) =
                    copyValue(separator, newValue)
            }
    }
}