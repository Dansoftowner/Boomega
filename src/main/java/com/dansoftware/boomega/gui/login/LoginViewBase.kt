package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.db.DatabaseMeta
import javafx.beans.value.ObservableStringValue
import javafx.scene.Group
import javafx.scene.input.TransferMode
import javafx.scene.layout.StackPane
import java.io.File

class LoginViewBase(private val controller: LoginBox.Controller) : StackPane() {

    private val loginBox = LoginBox(controller)

    init {
        styleClass.add("login-form")
        buildUI()
        enableDragSupport()
    }

    fun titleProperty(): ObservableStringValue = loginBox.titleProperty()

    private fun buildUI() {
        children.add(Group(loginBox))
    }

    private fun enableDragSupport() {
        setOnDragOver { event ->
            if (event.dragboard.hasFiles())
                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
        }
        setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.dragboard.files
                    .filter(File::isFile)
                    .map(::DatabaseMeta)
                    .toList()
                    .apply {
                        forEach(controller.databaseTracker::addDatabase)
                        lastOrNull()?.let { loginBox.selectedItem = it }
                    }
            }
        }
    }

}