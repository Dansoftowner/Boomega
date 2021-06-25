package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.stage.WindowEvent
import org.apache.commons.lang3.StringUtils
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
    private val root: LoginView,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) : BaseWindow(TitleProperty("window.login.title", " - ", root.titleProperty()), root, { root.context }),
    EventHandler<WindowEvent> {

    init {
        Objects.requireNonNull(preferences)
        Objects.requireNonNull(root, "LoginView shouldn't be null")
        this.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this)
        this.exitDialog = true
        this.isMaximized = true
        this.minWidth = 530.0
        this.minHeight = 530.0
        initKeyBindings()
    }

    private fun initKeyBindings() {
        GlobalActions.applyOnScene(scene, root.context, preferences, databaseTracker)
    }

    override fun handle(event: WindowEvent) {
        logger.debug("Putting loginData to Preferences")
        preferences.editor().put(PreferenceKey.LOGIN_DATA, root.loginData)
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

                private fun copyValue(separator: String, newValue: String) {
                    when (newValue) {
                        "null" -> this.set(StringUtils.EMPTY)
                        else -> this.set(separator.plus(newValue))
                    }
                }

                override fun changed(observable: ObservableValue<out String>, oldValue: String, newValue: String) =
                    copyValue(separator, newValue)
            }
    }
}