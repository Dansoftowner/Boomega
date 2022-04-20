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

package com.dansoftware.boomega.gui.info

import com.dansoftware.boomega.util.os.OsInfo

/**
 * Returns the string that contains the valuable information about the application
 */
internal fun getApplicationInfoCopy(): String =
    """Version: ${System.getProperty("boomega.version")}
       |${System.getProperty("boomega.build.info")} 
       ------
       |OS: ${OsInfo.name}
       |OS Version: ${OsInfo.version}
       |OS Build: ${OsInfo.buildNumber}
       ------
       |Java VM: ${System.getProperty("java.vm.name")}
       |Java VM version: ${System.getProperty("java.vm.version")}
       |Java VM vendor: ${System.getProperty("java.vm.vendor")}
       |Java version: ${System.getProperty("java.version")}
       |Java vendor: ${System.getProperty("java.vendor")}
       ------
       |JavaFX version: ${System.getProperty("javafx.version")}
       |JavaFX runtime version: ${System.getProperty("javafx.runtime.version")}
    """.trimMargin()