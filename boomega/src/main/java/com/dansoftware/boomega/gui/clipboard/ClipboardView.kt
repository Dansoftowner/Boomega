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

package com.dansoftware.boomega.gui.clipboard

import com.dansoftware.boomega.gui.api.EmptyContext
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class ClipboardView : VBox(), com.dansoftware.boomega.gui.api.EmptyContext {

    val table = buildTable()
    val toolbar = ClipboardViewToolbar(this)

    init {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        buildUI()
    }

    private fun buildUI() {
        children.add(toolbar)
        children.add(table)
    }

    private fun buildTable() = ClipboardTable().also {
        setVgrow(it, Priority.ALWAYS)
    }

    fun releaseListeners() {
        toolbar.releaseListeners()
        table.releaseListeners()
    }

}