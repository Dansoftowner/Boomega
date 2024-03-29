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

package com.dansoftware.boomega.gui.api

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.Hyperlink
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Duration
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.NonBlocking
import java.util.function.Consumer

/**
 * A Context is an abstract structure that allows to interact with a particular GUI environment
 * <i>(making alerts, showing notifications, etc...)</i>.
 *
 * @author Daniel Gyoerffy
 */
interface Context {

    val contextScene: Scene?
    val contextWindow: Window?
    val blockingOverlaysShown: ObservableList<Region>
    val nonBlockingOverlaysShown: ObservableList<Region>

    /**
     * Shows a popup (on the center) with the GUI-element defined.
     *
     * @param region   the {@link Region} GUI element to be shown
     * @param blocking {@code false} if clicking outside of the popup should close it.
     */
    @NonBlocking
    fun showOverlay(region: Region, blocking: Boolean)

    /**
     * @see [showOverlay]
     */
    @NonBlocking
    fun showOverlay(region: Region) {
        showOverlay(region, false)
    }

    /**
     * Hides the popup (if it's showing).
     *
     * @param region to be hidden
     */
    fun hideOverlay(region: Region)


    /**
     * Shows an error alert dialog.
     *
     * @param title    the title of the dialog
     * @param message  the message of the dialog
     * @param onResult the action when the user clicks the OK button
     */
    @NonBlocking
    fun showErrorDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>
    ): ContextDialog

    /**
     * Shows an error alert dialog. Also, it can show
     * the stacktrace of an exception.
     *
     * @param title     the title of the dialog
     * @param message   the message of the dialog
     * @param Exception? the Exception? that caused the dialog
     * @param onResult  the action when the user clicks the OK button
     */
    @NonBlocking
    fun showErrorDialog(
        title: String?,
        message: String?,
        cause: Exception?,
        onResult: Consumer<ButtonType>
    ): ContextDialog

    @NonBlocking
    fun showErrorDialog(title: String, message: String) {
        this.showErrorDialog(title, message) {}
    }

    @NonBlocking
    fun showErrorDialog(title: String, message: String, cause: Exception?) {
        this.showErrorDialog(title, message, cause) {}
    }

    /**
     * Shows an information dialog.
     *
     * @param title    the title
     * @param message  the message
     * @param onResult the action that handles the button click-s on the dialog
     */
    @NonBlocking
    fun showInformationDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>
    ): ContextDialog

    @NonBlocking
    fun showConfirmationDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>
    ): ContextDialog

    @NonBlocking
    fun showDialog(
        title: String?,
        content: Node,
        onResult: Consumer<ButtonType>,
        vararg buttonTypes: ButtonType
    ): ContextDialog

    @Blocking
    fun showErrorDialogAndWait(title: String?, message: String?): ButtonType

    @Blocking
    fun showErrorDialogAndWait(title: String?, message: String?, e: Exception?): ButtonType

    @Blocking
    fun showInformationDialogAndWait(title: String?, message: String?): ButtonType

    @Blocking
    fun showConfirmationDialogAndWait(title: String?, message: String?): ButtonType

    @Blocking
    fun showDialogAndWait(title: String?, content: Node, vararg buttonTypes: ButtonType): ButtonType

    @NonBlocking
    fun showErrorNotification(title: String?, message: String?)

    @NonBlocking
    fun showErrorNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>)

    @NonBlocking
    fun showErrorNotification(title: String?, message: String?, duration: Duration)

    @NonBlocking
    fun showErrorNotification(title: String?, message: String?, duration: Duration, onClicked: EventHandler<MouseEvent>)

    @NonBlocking
    fun showWarningNotification(title: String?, message: String?)

    @NonBlocking
    fun showWarningNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>)

    @NonBlocking
    fun showWarningNotification(title: String?, message: String?, duration: Duration)

    @NonBlocking
    fun showWarningNotification(
        title: String?,
        message: String?,
        duration: Duration,
        onClicked: EventHandler<MouseEvent>
    )

    @NonBlocking
    fun showInformationNotification(title: String?, message: String?)

    @NonBlocking
    fun showInformationNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>)

    @NonBlocking
    fun showInformationNotification(
        title: String?,
        message: String?,
        onClicked: EventHandler<MouseEvent>,
        vararg hyperlinks: Hyperlink
    )

    @NonBlocking
    fun showInformationNotification(title: String?, message: String?, duration: Duration)

    @NonBlocking
    fun showInformationNotification(
        title: String?,
        message: String?,
        duration: Duration,
        onClicked: EventHandler<MouseEvent>
    )

    @NonBlocking
    fun showIndeterminateProgress()

    @NonBlocking
    fun stopProgress()

    @NonBlocking
    fun showProgress(done: Long, max: Long, type: ProgressType)

    fun sendRequest(request: Request) {}

    /**
     * Requests focus on the particular GUI environment (usually on a {@link javafx.stage.Window}).
     */
    fun focusRequest()

    /**
     * Brings the particular GUI environment to front (usually with the {@link javafx.stage.Stage#toFront()})
     */
    fun toFrontRequest()

    fun isShowing(): Boolean

    fun onWindowPresent(action: Consumer<Window>)

    /**
     * Returns {@code true} if the {@link Context} is actually performs actions on a particular gui.
     */
    fun isReachable(): Boolean = true

    fun close() {
        val window = contextWindow
        if (window is Stage) window.close()
        else window?.hide()
    }

    enum class ProgressType {
        ERROR,
        NORMAL,
        PAUSED
    }

    interface Request
}