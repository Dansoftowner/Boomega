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
        Arrays.stream(urLs)
            .map(URL::toExternalForm)
            .peek { logger.debug("Plugin file found: {}", it)}
            .filter { it.startsWith("file:/") }
            .map { it.substring(6) }
            .filter { it.endsWith(".jar") }
            .map(::JarFile)
            .forEach { jar ->
                jar.entries().toList()
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
