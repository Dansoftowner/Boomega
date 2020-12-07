package com.dansoftware.libraryapp.gui.context

import javafx.scene.Node
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region
import javafx.stage.Window
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

    override fun getContextWindow(): Window? = null

    override fun requestFocus() {}

    override fun toFront() {}

    override fun isShowing(): Boolean = false

    override fun showIndeterminateProgress() {}

    override fun stopProgress() {}

    override fun showProgress(done: Long, max: Long, type: Context.ProgressType) {}

    override fun isReachable(): Boolean = false
}