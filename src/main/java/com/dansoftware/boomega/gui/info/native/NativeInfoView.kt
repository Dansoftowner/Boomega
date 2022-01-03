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

import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.gui.util.addRow
import com.dansoftware.boomega.gui.util.colspan
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.GridPane
import oshi.SystemInfo
import oshi.software.os.OperatingSystem

class NativeInfoView : GridPane() {

    private val systemInfo = SystemInfo()
    private val OperatingSystem.currentProcess get() = getProcess(processId)

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        addRow(Label("PID: "), buildPIDLabel())
        addRow(Label("UID: "), buildUIDLabel())
        addRow(Label("PRI: "), buildPRILabel())
        addRow(Label("(Owner)Username: "), buildUsernameLabel())
        addRow(Label("Elevated: "), buildElevatedLabel())
        addRow(Label("Command line: "), buildCommandLabel())
        addRow(Separator().colspan(2))
        addRow(Label("OS Manufacturer: "), buildOSManufacturerLabel())
        addRow(Label("Bitness: "), buildBitnessLabel())
    }

    private fun buildPIDLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.processId.toString()
    }

    private fun buildUIDLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.currentProcess.userID
    }

    private fun buildPRILabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.currentProcess.parentProcessID.toString()
    }

    private fun buildElevatedLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.isElevated.toString()
    }

    private fun buildCommandLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.currentProcess.commandLine
    }

    private fun buildUsernameLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.currentProcess.user
    }

    private fun buildOSManufacturerLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.manufacturer.toString()
    }

    private fun buildBitnessLabel() = HighlightableLabel().apply {
        text = systemInfo.operatingSystem.bitness.toString()
    }

    private fun buildFileSystemLabel() = Label().apply {
//        text = systemInfo.operatingSystem.
    }


}