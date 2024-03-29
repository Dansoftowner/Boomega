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

package com.dansoftware.boomega.gui.control

import javafx.scene.Cursor
import javafx.scene.control.TextField

class HighlightableLabel(text: String? = null) : TextField(text) {
    init {
        this.styleClass.clear()
        this.styleClass.add("highlightable-label")
        this.cursor = Cursor.TEXT
        this.style = "-fx-background-color: transparent;-fx-padding: 0;"
        this.prefColumnCount = 15
        this.styleClass.add("label")
        this.isEditable = false
        this.setOnContextMenuRequested { it.consume() }
    }
}