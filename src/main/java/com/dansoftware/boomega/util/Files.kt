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
import com.juserdirs.UserDirectories
import org.apache.commons.io.FileUtils
import java.awt.Desktop
import java.io.File
import java.net.URL
import java.nio.file.InvalidPathException
import java.awt.Desktop.getDesktop as desktop
import java.lang.Runtime.getRuntime as runtime

/**
 * Checks if the file's path is valid or not (it has no special characters etc...).
 */
val File.hasValidPath: Boolean
    get() {
        return try {
            toPath() //ignored result
            true
        } catch (e: InvalidPathException) {
            false
        } catch (e: NullPointerException) {
            false
        }
    }

/**
 * Gets the [File] representation of the system's temporary directory
 */
val tempDirectory by lazy {
    File(System.getProperty("java.io.tmpdir"))
}

/**
 * Gets the user home directory's path
 */
val userDirectoryPath: String get() = System.getProperty("user.home")

/**
 * Gets the system's default "Documents" directory-path, if exists
 */
val documentsDirectoryPath: String? get() = UserDirectories.get().documentsDirectoryPath()

/**
 * Gives a directory separated string of the specified elements.
 * Can be useful for building file paths.
 */
fun joinToFilePath(vararg elements: String): String =
    elements.joinToString(File.separator)

/**
 * Opens a folder containing the file and selects it in a default system file manager.
 */
fun File.revealInExplorer() {
    fun invokeNativeCommand() {
        when {
            OsInfo.isWindows() -> runtime().exec("explorer.exe /select, \"$path\"")
            OsInfo.isLinux() -> runtime().exec(arrayOf("nautilus", path))
            OsInfo.isMac() -> runtime().exec(arrayOf("open", "-a", "Finder", path)) // alternatively: open -R <file path>
        }
    }

    CachedExecutor.submit {
        when {
            desktop().isSupported(Desktop.Action.BROWSE_FILE_DIR) -> desktop().browseFileDirectory(this)
            else -> invokeNativeCommand()
        }
    }
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

/**
 * Creates a shortened path from a file.
 *
 * It is useful when you don't want to use a very long file-path, and you want to short it.
 *
 * ### Examples
 *
 * Let's say you have a file-path: `/images/background/wallpaper/design/First.png`, and you want to create a
 * user-visible string that only shows the 2 previous directory: `.../wallpaper/design/First.png`.
 * Then you can do this:
 * ```kotlin
 * file.shortenedPath(maxBack = 2, prefix = "...", separator = "/")
 * ```
 * *More examples*
 * ```kotlin
 * val file = File("programFiles/thePrg/appdata/inf/config.prop")
 * file.shortenedPath(0, "<DEFAULT>", "\\") // "<DEFAULT>\config.prop"
 * ```
 * ```kotlin
 * val file = File("users/user0/documents/Plans.docx")
 * file.shortenedPath(1, "[USER_HOME]", "/") // "[USER_HOME]/documents/Plans.docx"
 * ```
 *
 * @param prefix    the prefix that will be at the start of the string;
 *                  basically what hides the directories that are not displayed
 * @param separator the separator character that will separate the directory names.
 * @param maxBack   specifies the maximum number of directories we want to display before the file's name
 * @return the shortened path
 */
fun File.shortenedPath(maxBack: Int, prefix: String = "...", separator: String = File.separator): String {
    require(maxBack > 0)

    val stringBuilder = StringBuilder(name)

    var i = maxBack
    var lastParent: File? = parentFile
    while (i > 0 && lastParent != null) {
        stringBuilder.insert(0, separator).insert(0, lastParent.name)
        lastParent = lastParent.parentFile
        i--
    }

    lastParent?.let {
        stringBuilder.insert(0, separator).insert(0, prefix)
    }

    return stringBuilder.toString()
}

/**
 * Returns a human-readable version of the file size
 */
fun File.byteCountToDisplaySize(): String {
    return byteCountToDisplaySize(FileUtils.sizeOf(this))
}

fun byteCountToDisplaySize(size: Long): String {
    return FileUtils.byteCountToDisplaySize(size)
}

/**
 * Converts the array of [File]s into an array of [URL]s
 */
fun Array<File>.toURLS(): Array<URL> {
    return Array(size) { this[it].toURI().toURL() }
}