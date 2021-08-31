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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.db.DatabaseMeta
import javafx.beans.value.ObservableStringValue
import javafx.scene.Group
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.io.File

class LoginViewBase(private val controller: LoginBox.Controller) : VBox() {

    private val loginBox = LoginBox(controller)

    init {
        styleClass.add("login-form")
        buildUI()
        enableDragSupport()
    }

    fun titleProperty(): ObservableStringValue = loginBox.titleProperty()

    private fun buildUI() {
        children.add(LoginToolbar(controller.context, controller.databaseTracker, controller.preferences))
        children.add(StackPane(Group(loginBox)).also { setVgrow(it, Priority.ALWAYS) })
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
                        forEach(controller.databaseTracker::saveDatabase)
                        lastOrNull()?.let { loginBox.selectedItem = it }
                    }
            }
        }
    }

}