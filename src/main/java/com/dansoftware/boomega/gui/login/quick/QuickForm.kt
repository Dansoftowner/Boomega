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

package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.db.NitriteDatabase
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.login.DatabaseLoginListener
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class QuickForm(
    private val context: Context,
    private val databaseMeta: DatabaseMeta,
    private val loginListener: DatabaseLoginListener
) : VBox(10.0) {

    private val usernameInput: StringProperty = SimpleStringProperty()
    private val passwordInput: StringProperty = SimpleStringProperty()

    init {
        setMargin(this, Insets(10.0))
        setVgrow(this, Priority.ALWAYS)
        buildUI()
    }

    private fun buildUI() {
        children.add(buildUsernameInputField())
        children.add(buildPasswordInputField())
        children.add(buildLoginButton())
    }

    private fun buildUsernameInputField() = TextField().apply {
        minHeight = 35.0
        promptText = i18n("credentials.username")
        usernameInput.bind(textProperty())
    }

    private fun buildPasswordInputField() = PasswordField().apply {
        minHeight = 35.0
        promptText = i18n("credentials.password")
        passwordInput.bind(textProperty())
    }

    private fun buildLoginButton() = Button().apply {
        text = i18n("login.form.login")
        minHeight = 35.0
        isDefaultButton = true
        minWidth = 400.0
        maxWidth = Double.MAX_VALUE
        setOnAction { login() }

    }

    private fun login() {
        Credentials(
            StringUtils.trim(usernameInput.get()),
            StringUtils.trim(passwordInput.get())
        ).let { credentials ->
            NitriteDatabase.builder()
                .databaseMeta(databaseMeta)
                .onFailed { message, t ->
                    context.showErrorDialog(i18n("login.failed"), message, t as Exception?)
                    logger.error("Failed to create/open the database", t)
                }.build(credentials)?.let {
                    logger.debug("Quick login in was successful")
                    loginListener.onDatabaseOpened(it)
                    context.close()
                }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(QuickForm::class.java)
    }
}