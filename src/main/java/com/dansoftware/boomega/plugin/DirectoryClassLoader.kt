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

package com.dansoftware.boomega.plugin

import com.dansoftware.boomega.util.toURLS
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * The [DirectoryClassLoader] can load classes from both the class-path and the jar files located in
 * the given directory.
 *
 * @param directory the [File] representing the directory
 */
@Singleton
class DirectoryClassLoader @Inject constructor(@Named("jarDirectory") directory: File) : URLClassLoader(
    directory.listFiles { _, name -> JAR_FILE_PATTERN.matches(name) }?.toURLS(),
    getSystemClassLoader()
) {

    /**
     * Lists all the classes loaded from the jar files
     */
    fun listAllClasses(): List<Class<*>> =
        urLs.asSequence()
            .map(URL::toExternalForm)
            .onEach { logger.debug("Plugin file found: {}", it) }
            .filter { it.startsWith("file:/") }
            .map { it.substring(5) } // TODO: debug it on Windows
            .filter { it.endsWith(".jar") }
            .map(::JarFile)
            .flatMap { jar ->
                jar.entries().asSequence()
                    .filter { it.isDirectory.not() && it.name.endsWith(".class") }
                    .map(JarEntry::getName)
                    .map { it.substring(0, it.length - 6) }
                    .map { it.replace('/', '.') }
                    .map(::findClass)
            }
            .toList()

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(DirectoryClassLoader::class.java)
        private val JAR_FILE_PATTERN = Regex(".*\\.jar", RegexOption.IGNORE_CASE)
    }
}
