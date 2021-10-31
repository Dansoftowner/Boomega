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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import javafx.beans.value.ObservableStringValue
import javafx.scene.Group
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class LoginViewBase(
    private val context: Context,
    private val databaseTracker: DatabaseTracker,
    private val preferences: Preferences,
    private val databaseLoginListener: DatabaseLoginListener
) : VBox() {

    private val loginBox = LoginBox(context, databaseTracker, databaseLoginListener)

    init {
        styleClass.add("login-form")
        buildUI()
        enableDragSupport()
    }

    fun titleProperty(): ObservableStringValue = loginBox.titleProperty()

    private fun buildUI() {
        children.add(LoginToolbar(context, databaseTracker, preferences))
        children.add(StackPane(Group(loginBox)).also { setVgrow(it, Priority.ALWAYS) })
    }

    private fun enableDragSupport() {
        setOnDragOver { event ->
            if (event.dragboard.hasFiles())
                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
        }
        setOnDragDropped { event ->
            /*  if (event.dragboard.hasFiles()) {
                  event.dragboard.files
                      .filter(File::isFile)
                      .map(::DatabaseMeta)
                      .toList()
                      .apply {
                          forEach(controller.databaseTracker::saveDatabase)
                          lastOrNull()?.let { loginBox.selectedItem = it }
                      }
              }*/
        }
    }

}