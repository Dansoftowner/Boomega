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

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.TitledPane
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea

class ExceptionDisplayPane(exception: Exception?) : TitledPane() {
    init {
        content = buildCodeArea(exception)
        isAnimated = true
        isExpanded = false
    }

    private fun buildCodeArea(exception: Exception?): Node =
        CodeArea(exception?.stackTraceToString()).run {
            padding = Insets(5.0)
            isEditable = false
            prefHeight = 200.0
            VirtualizedScrollPane(this)
        }
}