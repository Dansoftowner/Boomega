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

package com.dansoftware.boomega.main

import com.dansoftware.boomega.util.documentsDirectoryPath
import com.dansoftware.boomega.util.joinToFilePath
import com.dansoftware.boomega.util.os.OsInfo
import com.dansoftware.boomega.util.tempDirectory
import com.dansoftware.boomega.util.userDirectoryPath
import java.io.File

/**
 * Responsible for adding the necessary system properties that are needed for the application.
 *
 * @author Daniel Gyorffy
 */
object PropertiesSetup {

    private const val APP_NAME = "app.name"
    private const val BOOMEGA_VERSION = "boomega.version"
    private const val BOOMEGA_BUILD_INFO = "boomega.build.info"
    private const val BOOMEGA_FILE_EXTENSION = "boomega.file.extension"
    private const val LOG_FILE_PATH = "log.file.path"
    private const val LOG_FILE_FULL_PATH = "log.file.path.full"
    private const val PLUGIN_DIRECTORY_PATH = "boomega.plugin.dir"
    private const val DEFAULT_DIRECTORY_PATH = "boomega.dir.default.path"

    private const val APP_NAME_VALUE = "Boomega"
    private const val BOOMEGA_VERSION_VALUE = "0.8.0"
    private const val BOOMEGA_FILE_EXTENSION_VALUE = "bmdb"
    private const val BOOMEGA_DOCUMENTS_FOLDER = "BoomegaDocuments"

    /**
     * The log-file's path without the extension (for the logback configuration)
     */
    private val LOG_FILE_PATH_VALUE = File(tempDirectory, "boomega").toString()

    /**
     * The log-file's path with the extension
     */
    private val LOG_FILE_FULL_PATH_VALUE: String = "$LOG_FILE_PATH_VALUE.log"

    /**
     * Puts all the necessary system-properties that the application needs.
     */
    @JvmStatic
    fun setupSystemProperties() {
        putLogFileProperties()
        adjustJavaLibraryPath()
        putJFXProperties()
        putAppSpecificProperties()
    }

    /**
     * Adjusts the *java library path* by prefixing it with the current java runtimes bin folder
     * to make sure that later the JavaFX platform loads the native *.dll* libraries from the right
     * place. **Windows only**. It has meaning only if the program runs as a native app.
     * See [**#146**](https://github.com/Dansoftowner/Boomega/issues/146).
     */
    private fun adjustJavaLibraryPath() {
        if (OsInfo.isWindows()) {
            val javaLibraryPath = System.getProperty("java.library.path")
            val javaRuntime = System.getProperty("java.home")

            System.setProperty(
                "java.library.path",
                "$javaRuntime\\bin;$javaLibraryPath"
            )
        }
    }

    /**
     * Puts the log file information into the system properties
     */
    private fun putLogFileProperties() {
        System.setProperty(LOG_FILE_PATH, LOG_FILE_PATH_VALUE)
        System.setProperty(LOG_FILE_FULL_PATH, LOG_FILE_FULL_PATH_VALUE)
    }

    /**
     * Puts some javaFX information into the system properties
     */
    private fun putJFXProperties() = com.sun.javafx.runtime.VersionInfo.setupSystemProperties()

    /**
     * Puts the boomega-specific values into the system properties
     */
    private fun putAppSpecificProperties() {
        System.setProperty(APP_NAME, APP_NAME_VALUE)
        System.setProperty(BOOMEGA_VERSION, BOOMEGA_VERSION_VALUE)
        System.setProperty(BOOMEGA_FILE_EXTENSION, BOOMEGA_FILE_EXTENSION_VALUE)
        System.setProperty(PLUGIN_DIRECTORY_PATH, getPluginDirPath())
        System.setProperty(DEFAULT_DIRECTORY_PATH, getDefaultDirectoryFilePath())
    }

    private fun getDefaultDirectoryFilePath() =
        File(
            documentsDirectoryPath ?: System.getProperty("user.home"),
            BOOMEGA_DOCUMENTS_FOLDER
        ).absolutePath

    /**
     * Returns the plugin directory's path
     */
    private fun getPluginDirPath(): String = when {
        OsInfo.isWindows -> {
            val appdata = System.getenv("APPDATA")?.takeIf { it.isNotBlank() } ?: userDirectoryPath
            File(File(appdata), "Dansoftware\\boomega\\plugin").absolutePath
        }
        OsInfo.isLinux -> "$userDirectoryPath/boomega/plugin"
        OsInfo.isMacOS -> "$userDirectoryPath/boomega/plugin"
        else -> "plugin"
    }
}