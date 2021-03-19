package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.db.NitriteDatabase
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.login.DatabaseLoginListener
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
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
        padding = Insets(10.0)
        setMargin(this, Insets(0.0, 20.0, 20.0, 20.0))
        buildUI()
    }

    private fun buildUI() {
        children.add(buildUsernameInputField())
        children.add(buildPasswordInputField())
        children.add(buildLoginButton())
    }

    private fun buildUsernameInputField() = TextField().apply {
        minHeight = 35.0
        promptText = I18N.getValue("login.form.username.prompt")
        usernameInput.bind(textProperty())
    }

    private fun buildPasswordInputField() = PasswordField().apply {
        minHeight = 35.0
        promptText = I18N.getValue("login.form.password.prompt")
        passwordInput.bind(textProperty())
    }

    private fun buildLoginButton() = Button().apply {
        text = I18N.getValue("login.form.login")
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
            NitriteDatabase.getAuthenticator()
                .onFailed { title, message, t ->
                    context.showErrorDialog(title, message, t as Exception?)
                    logger.error("Failed to create/open the database", t)
                }.auth(databaseMeta, credentials)?.let {
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