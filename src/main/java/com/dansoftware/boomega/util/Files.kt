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

@file:JvmName("FileUtils")

package com.dansoftware.boomega.util

import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.dansoftware.boomega.util.os.OsInfo
import java.awt.Desktop
import java.io.File

import java.awt.Desktop.getDesktop as desktop
import java.lang.Runtime.getRuntime as runtime

/**
 * Opens a folder containing the file and selects it in a default system file manager.
 */
fun File.revealInExplorer() {
    CachedExecutor.submit {
        when {
            desktop().isSupported(Desktop.Action.BROWSE_FILE_DIR) -> desktop().browseFileDirectory(this)
            else -> invokeSelectCommand()
        }
    }
}

private fun File.invokeSelectCommand() =
    when {
        OsInfo.isWindows() -> runtime().exec("explorer.exe /root, \"$path\"")
        OsInfo.isLinux() -> runtime().exec(arrayOf("nautilus", path))
        OsInfo.isMac() -> runtime().exec(arrayOf("open", "-a", "Finder", path))
        else -> null
    }

/**
 * Launches the associated application to open the file.
 */
fun File.open() {
    CachedExecutor.submit {
        if (desktop().isSupported(Desktop.Action.OPEN))
            desktop().open(this)
        else runtime().exec(absoluteFile.absolutePath)
    }
}

/**
 * Determines whether the file is an executable by the operating system
 * based on the following conditions:
 * - on Windows the executable file extensions are: "exe" and "msi"
 * - on Mac the executable file extensions are: "dmg" and "app"
 * - on Linux the executable file extensions are: "deb" and "rpm"
 */
fun File.isExecutable(): Boolean {
    return extension in when {
        OsInfo.isWindows() -> listOf("exe", "msi")
        OsInfo.isMac() -> listOf("dmg", "app")
        OsInfo.isLinux() -> listOf("deb", "rpm")
        else -> emptyList()
    }
}