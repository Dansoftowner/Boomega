/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.info.native

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import oshi.SystemInfo

class NativeInfoView : GridPane() {

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.addAll(
            Label("PID: "),
            buildPIDValueLabel()
        )
    }

    private fun buildPIDValueLabel() = Label().apply {
        text = SystemInfo().operatingSystem.processId.toString()
        setColumnIndex(this, 1)
    }
}