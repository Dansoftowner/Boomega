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

import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * The [PluginClassLoader] can load classes from both the class-path and the third-party plugins located
 * in the plugin-directory.
 *
 * @author Daniel Gyorffy
 */
object PluginClassLoader : URLClassLoader(PluginDirectory.getPluginFilesAsUrls(), getSystemClassLoader()) {

    @JvmStatic
    private val logger = LoggerFactory.getLogger(javaClass)

    val readPluginsCount: Int
        get() = this.urLs.size

    /**
     * For already existing code bases
     */
    @JvmStatic
    fun getInstance(): PluginClassLoader = this

    fun isEmpty(): Boolean = PluginDirectory.isEmpty()

    override fun close() {
        super.close()
        PluginDirectory.clear()
    }

    fun listAllClasses(): List<Class<*>> = LinkedList<Class<*>>().run {
        urLs.asSequence()
            .map(URL::toExternalForm)
            .onEach { logger.debug("Plugin file found: {}", it) }
            .filter { it.startsWith("file:/") }
            .map { it.substring(6) }
            .filter { it.endsWith(".jar") }
            .map(::JarFile)
            .forEach { jar ->
                jar.entries().asSequence()
                    .filter { it.isDirectory.not() && it.name.endsWith(".class") }
                    .map(JarEntry::getName)
                    .map { it.substring(0, it.length - 6) }
                    .map { it.replace('/', '.') }
                    .map { this@PluginClassLoader.findClass(it) }
                    .let { addAll(it) }
            }
        Collections.unmodifiableList(this)
    }
}
