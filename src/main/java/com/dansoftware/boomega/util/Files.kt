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

import com.jfilegoodies.explorer.FileExplorers
import java.awt.Desktop
import java.io.File

private inline val desktop
    get() = Desktop.getDesktop()

/**
 * Opens a folder containing the file and selects it in a default system file manager.
 */
fun File.revealInExplorer() {
    when {
        desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR) -> desktop.browseFileDirectory(this)
        else -> FileExplorers.get().openSelect(this)
    }
}

/**
 * Launches the associated application to open the file.
 */
fun File.open() {
    desktop.open(this)
}