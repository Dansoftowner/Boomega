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

package com.dansoftware.boomega.plugin

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.util.regex.Pattern

/**
 * Represents the directory that holds the plugin jar files on the disk.
 *
 * @author Daniel Gyorffy
 */
object PluginDirectory {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val JAR_EXTENSION_PATTERN = Pattern.compile(".*\\.jar", Pattern.CASE_INSENSITIVE)

    private val directory = File(System.getProperty("boomega.plugin.dir"))
    private val registrationFile: File
    private val registeredFiles: MutableList<String>

    init {
        directory.mkdirs()
        registrationFile = File(directory.parent, "registeredplugins.conf")
        registrationFile.createNewFile()
        registeredFiles = ArrayList(registrationFile.readLines())
    }

    private fun registerPlugin(file: File) {
        registeredFiles.add(file.name)
        registrationFile.writeText(registeredFiles.joinToString("\n"))
    }

    /**
     * Lists the plugin archives as [File] objects
     */
    fun getPluginFiles(): Array<File> {
        return directory.listFiles { _, name ->
            JAR_EXTENSION_PATTERN.matcher(name).matches() && registeredFiles.contains(name)
        } ?: emptyArray()
    }

    /**
     * Lists the plugin archives as [URL] objects
     */
    fun getPluginFilesAsUrls(): Array<URL>? {
        return try {
            FileUtils.toURLs(*getPluginFiles())
        } catch (e: IOException) {
            null
        }
    }

    fun unregisterPlugin(toRemove: File) {
        registeredFiles.remove(toRemove.name)
        registrationFile.writeText(registeredFiles.joinToString("\n"))
    }

    @Throws(IOException::class)
    fun addPlugin(src: File) {
        try {
            Files.copy(src.toPath(), File(directory, src.name).toPath())
            registerPlugin(src)
        } catch (e: java.nio.file.FileAlreadyExistsException) {
            registerPlugin(src)
        }
    }

    fun clear() {
        val newRegistrationFileContent = ArrayList<String>()
        registeredFiles.forEach {
            if (File(directory, it).exists()) {
                newRegistrationFileContent.add(it)
            }
        }
        directory.listFiles { _, name -> JAR_EXTENSION_PATTERN.matcher(name).matches() }?.forEach {
            if (newRegistrationFileContent.contains(it.name).not()) {
                try {
                    Files.deleteIfExists(it.toPath())
                } catch (e: IOException) {
                    logger.error("Couldn't delete plugin file ", e)
                }
            }
        }
    }

    fun isEmpty(): Boolean = registeredFiles.isEmpty()
}