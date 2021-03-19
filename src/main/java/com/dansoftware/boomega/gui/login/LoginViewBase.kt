package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.appdata.logindata.LoginData
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import javafx.beans.value.ObservableStringValue
import javafx.scene.Group
import javafx.scene.input.TransferMode
import javafx.scene.layout.StackPane
import java.io.File

class LoginViewBase(
    private val context: Context,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker,
    val loginData: LoginData,
    private val databaseLoginListener: DatabaseLoginListener
) : StackPane() {

    private val loginBox = LoginBox(
        context,
        preferences,
        databaseTracker,
        loginData,
        databaseLoginListener
    )

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
                event.dragboard.files.stream()
                    .filter(File::isFile)
                    .map(::DatabaseMeta)
                    .forEach(databaseTracker::addDatabase)
            }
        }
    }

}