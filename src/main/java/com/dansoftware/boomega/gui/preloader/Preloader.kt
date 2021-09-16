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

package com.dansoftware.boomega.gui.preloader

import com.dansoftware.boomega.gui.font.CustomFontsLoader
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory

class Preloader : javafx.application.Preloader() {

    private val progressProperty = SimpleDoubleProperty()
    private val messageProperty = SimpleStringProperty()
    private lateinit var window: Stage

    override fun start(primaryStage: Stage?) {
        initApplicationName()

        val ui = PreloaderView()
        ui.messageProperty.bind(messageProperty)
        ui.progressProperty.bind(progressProperty)
        window = Stage(StageStyle.TRANSPARENT)
            .apply {
                scene = Scene(ui).apply {
                    stylesheets.add(STYLESHEET)
                    fill = Color.TRANSPARENT
                }
                centerOnScreen()
            }
            .shadowed()

        window.show()
    }

    private fun initApplicationName() {
        // Fixes the wrong app name on the top bar issue on Gnome systems
        // See more details: https://github.com/Dansoftowner/Boomega/issues/111
        com.sun.glass.ui.Application.GetApplication().name = System.getProperty("app.name");
    }

    override fun handleStateChangeNotification(info: StateChangeNotification) {
        when (info.type) {
            StateChangeNotification.Type.BEFORE_START -> window.close()
        }
    }

    override fun handleApplicationNotification(info: PreloaderNotification) {
        when(info) {
            is MessageNotification ->
                if (!messageProperty.isBound) {
                    if (info.priority == MessageNotification.Priority.HIGH)
                        messageProperty.bind(SimpleStringProperty(info.message))
                    else
                        messageProperty.set(info.message)
                }
            is ProgressNotification -> progressProperty.set(info.progress)
            is HideNotification -> window.hide()
            is ShowNotification -> window.show()
        }
    }


    /**
     * Notification for hiding the preloader-window.
     */
    class HideNotification : PreloaderNotification

    /**
     * Notification for showing the preloader-window if its hidden.
     */
    class ShowNotification : PreloaderNotification

    /**
     * Notification for showing progress
     */
    class ProgressNotification(val progress: Double) : PreloaderNotification

    /**
     * Notification for sending messages to the preloader
     */
    open class MessageNotification @JvmOverloads constructor(
        val message: String,
        val priority: Priority = Priority.REGULAR
    ) : PreloaderNotification {
        enum class Priority {
            HIGH,
            REGULAR
        }
    }

    private fun Stage.shadowed() =
        Stage(StageStyle.UTILITY).apply {
            opacity = 0.0
            this@shadowed.initOwner(this)
            addEventHandler(WindowEvent.WINDOW_SHOWN) { this@shadowed.show() }
            addEventHandler(WindowEvent.WINDOW_HIDING) { this@shadowed.hide() }
        }

    companion object {

        private val logger = LoggerFactory.getLogger(Preloader::class.java)
        private const val STYLESHEET = "/com/dansoftware/boomega/gui/theme/preloader.css"

        init {
            CustomFontsLoader.loadFonts()
        }
    }
}