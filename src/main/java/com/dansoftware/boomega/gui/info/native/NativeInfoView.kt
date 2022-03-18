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
import com.dansoftware.boomega.util.byteCountToDisplaySize
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.GridPane
import oshi.SystemInfo
import oshi.software.os.OperatingSystem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gives an overview of the basic information about the process and the os.
 * > Note: it's not internationalized because it's not important for average users.
 */
class NativeInfoView : GridPane() {

    private val systemInfo = SystemInfo()
    private val os = systemInfo.operatingSystem
    private val OperatingSystem.currentProcess get() = getProcess(processId)
    private val currentProcess = systemInfo.operatingSystem.currentProcess

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        addRow(Label("PID: "), label(currentProcess.processID))
        addRow(Label("UID: "), label(currentProcess.userID))
        addRow(Label("PRI: "), label(currentProcess.parentProcessID))
        addRow(Label("(Owner)Username: "), label(currentProcess.user))
        addRow(Label("Elevated: "), label(os.isElevated))
        addRow(Label("Priority: "), label(currentProcess.priority))
        addRow(Label("Start time: "), label(currentProcess.startTime.formatMillisToDate("yyyy-MM-DD HH:mm")))
        addRow(Label("Bytes read: "), label(byteCountToDisplaySize(currentProcess.bytesRead)))
        addRow(Label("Bytes written: "), label(byteCountToDisplaySize(currentProcess.bytesWritten)))
        addRow(Label("Command line: "), label(currentProcess.commandLine))
        addRow(Label("Working directory: "), label(currentProcess.currentWorkingDirectory))
        addRow(Separator().colspan(2))
        addRow(Label("OS Name: "), label(os.family))
        addRow(Label("OS Manufacturer: "), label(os.manufacturer))
        addRow(Label("OS Version: "), label(os.versionInfo.version))
        addRow(Label("OS build number: "), label(os.versionInfo.buildNumber))
        addRow(Label("Bitness: "), label(os.bitness))
    }

    private fun label(value: Any?) = HighlightableLabel(value.toString())
    private fun Long.formatMillisToDate(format: String): String = SimpleDateFormat(format).format(Date(this))
}