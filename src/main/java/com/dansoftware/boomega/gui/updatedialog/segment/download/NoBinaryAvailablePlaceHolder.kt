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

package com.dansoftware.boomega.gui.updatedialog.segment.download

import com.dansoftware.boomega.i18n.I18N
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.Font

class NoBinaryAvailablePlaceHolder : StackPane() {
    init {
        styleClass.add("no-binary-available-place-holder")
        buildUI()
    }

    private fun buildUI() {
        children.add(CenterLabel(I18N.getValue("update.dialog.download.no_binary_available")))
    }

    private class CenterLabel(text: String) : Label(text) {
        init {
            font = Font.font(15.0)
        }
    }
}