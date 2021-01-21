package com.dansoftware.libraryapp.gui.context

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.stage.Window
import javafx.util.Duration
import java.util.function.Consumer

private class EmptyContext : Context {

    override fun showOverlay(region: Region?, blocking: Boolean) {}

    override fun hideOverlay(region: Region?) {}

    override fun showErrorDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>?
    ): ContextDialog = null!!

    override fun showErrorDialog(
        title: String?,
        message: String?,
        exception: Exception?,
        onResult: Consumer<ButtonType>?
    ): ContextDialog = null!!

    override fun showInformationDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>?
    ): ContextDialog = null!!

    override fun showConfirmationDialog(
        title: String?,
        message: String?,
        onResult: Consumer<ButtonType>?
    ): ContextDialog = null!!

    override fun showDialog(
        title: String?,
        content: Node?,
        onResult: Consumer<ButtonType>?,
        vararg buttonTypes: ButtonType?
    ): ContextDialog = null!!

    override fun showErrorDialogAndWait(title: String?, message: String?): ButtonType? = null

    override fun showErrorDialogAndWait(title: String?, message: String?, e: java.lang.Exception?): ButtonType? = null

    override fun showInformationDialogAndWait(title: String?, message: String?): ButtonType? = null

    override fun showConfirmationDialogAndWait(title: String?, message: String?): ButtonType? = null

    override fun showDialogAndWait(title: String?, content: Node?, vararg buttonTypes: ButtonType?): ButtonType? = null

    override fun showErrorNotification(title: String?, message: String?) {}

    override fun showErrorNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>?) {}

    override fun showErrorNotification(title: String?, message: String?, duration: Duration?) {}

    override fun showErrorNotification(
        title: String?,
        message: String?,
        duration: Duration?,
        onClicked: EventHandler<MouseEvent>?
    ) {}

    override fun showWarningNotification(title: String?, message: String?) {}

    override fun showWarningNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>?) {}

    override fun showWarningNotification(title: String?, message: String?, duration: Duration?) {}

    override fun showWarningNotification(
        title: String?,
        message: String?,
        duration: Duration?,
        onClicked: EventHandler<MouseEvent>?
    ) {}

    override fun showInformationNotification(title: String?, message: String?) {}

    override fun showInformationNotification(title: String?, message: String?, onClicked: EventHandler<MouseEvent>?) {}

    override fun showInformationNotification(title: String?, message: String?, duration: Duration?) {}

    override fun showInformationNotification(
        title: String?,
        message: String?,
        duration: Duration?,
        onClicked: EventHandler<MouseEvent>?
    ) {}

    override fun showModule(classRef: Class<*>) {}

    override fun <D> showModule(classRef: Class<out NotifiableModule<D>>, data: D) {}

    override fun getContextScene(): Scene? = null

    override fun getContextWindow(): Window? = null

    override fun requestFocus() {}

    override fun toFront() {}

    override fun isShowing(): Boolean = false

    override fun showIndeterminateProgress() {}

    override fun stopProgress() {}

    override fun showProgress(done: Long, max: Long, type: Context.ProgressType) {}

    override fun onWindowPresent(action: Consumer<Window>?) {}

    override fun isReachable(): Boolean = false
}