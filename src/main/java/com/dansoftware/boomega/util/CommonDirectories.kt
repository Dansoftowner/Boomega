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

package com.dansoftware.boomega.util

import com.dansoftware.boomega.util.os.OsInfo
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.io.File
import javax.swing.filechooser.FileSystemView

/**
 * Used for retrieving some common directory paths (like 'Documents', 'Downloads' etc..)
 */
object CommonDirectories {
    @JvmStatic
    val documentsDir: File?
        get() = FileSystemView.getFileSystemView().defaultDirectory

    @JvmStatic
    val downloadsDir: File?
        get() = when {
            OsInfo.isWindows() -> {
                val registryPath = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders"
                val registryValue = "{374DE290-123F-4565-9164-39C4925E467B}"

                if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, registryPath, registryValue))
                    File(Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, registryPath, registryValue))
                else null
            }
            else -> null
        }

    @JvmStatic
    val downloadsDirPath: String?
        get() = downloadsDir?.absolutePath
}