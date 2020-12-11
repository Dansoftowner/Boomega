package com.dansoftware.libraryapp.gui.login

import com.dansoftware.libraryapp.appdata.Preferences
import com.dansoftware.libraryapp.gui.window.BaseWindow
import com.dansoftware.libraryapp.locale.I18N
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import java.util.*

/**
 * A LoginWindow is a javaFX [Stage] that should be
 * used to display [LoginView] gui-objects.
 *
 *
 *
 * Also, when a user closes the LoginWindow, it will save the [LoginData] to the
 * configurations.
 */
private class LoginWindow(private val root: LoginView, private val preferences: Preferences) :
    BaseWindow<LoginView>("window.login.title", " - ", root.titleProperty(), root),
    EventHandler<WindowEvent> {

    init {
        Objects.requireNonNull(preferences)
        Objects.requireNonNull(root, "LoginView shouldn't be null")
        this.setFullScreenKeyCombination(KeyCodeCombination(KeyCode.F11))
        this.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this)
        this.exitDialog = true
        this.fullScreenExitHint = I18N.getGeneralValue("window.fullscreen.hint")
        this.isMaximized = true
        this.minWidth = 530.0
        this.minHeight = 530.0
    }

    override fun handle(event: WindowEvent) {
        logger.debug("Putting loginData to Preferences")
        preferences.editor().put(Preferences.Key.LOGIN_DATA, root.loginData)
    }

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(LoginWindow::class.java)
    }
}