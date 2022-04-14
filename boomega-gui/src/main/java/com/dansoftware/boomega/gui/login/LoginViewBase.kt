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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.database.bmdb.BMDBMeta
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker
import javafx.beans.value.ObservableStringValue
import javafx.scene.Group
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.io.File

class LoginViewBase(
    private val context: Context,
    private val databaseLoginListener: DatabaseLoginListener
) : VBox() {

    private val loginBox = LoginBox(context, databaseLoginListener)

    init {
        styleClass.add("login-form")
        buildUI()
        enableDragSupport()
    }

    fun titleProperty(): ObservableStringValue = loginBox.titleProperty()

    private fun buildUI() {
        children.add(StackPane(Group(loginBox)).also { setVgrow(it, Priority.ALWAYS) })
    }

    private fun enableDragSupport() {
        setOnDragOver { event ->
            if (event.dragboard.hasFiles())
                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
        }
        setOnDragDropped { event ->
            if (event.dragboard.hasFiles()) {
                event.dragboard.files.asSequence()
                    .filter(File::isFile)
                    .map(::BMDBMeta)
                    .onEach(get(DatabaseTracker::class)::saveDatabase)
                    .lastOrNull()
                    ?.let {
                        loginBox.selectedDatabase = it
                    }
            }
        }
    }

}