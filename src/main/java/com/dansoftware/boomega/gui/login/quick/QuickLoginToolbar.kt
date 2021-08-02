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

package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class QuickLoginToolbar(private val databaseMeta: DatabaseMeta) : HBox() {

    init {
        styleClass.add("header-toolbar")
        spacing = 10.0
        buildUI()
    }

    private fun buildUI() {
        children.add(icon("login-icon"))
        children.add(buildLabel())
    }

    private fun buildLabel() =
        Label("${I18N.getValue("login.quick.title")} - $databaseMeta").also {
            setHgrow(it, Priority.ALWAYS)
        }
}