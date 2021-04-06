package com.dansoftware.boomega.plugin

import com.dansoftware.boomega.util.ReflectionUtils
import org.apache.commons.lang3.StringUtils
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

    fun getAllClasses(): List<Class<*>> = LinkedList<Class<*>>().run {
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

    /**
     * Collects all subtypes of the given class
     */
    @Suppress("UNCHECKED_CAST")
    @Deprecated("")
    fun <T, S : T> getSubtypesOf(classRef: Class<T>): Set<Class<S>> {
        return when {
            this.isEmpty().not() -> ReflectionUtils.getSubtypesOf(classRef, this) as Set<Class<S>>
            else -> Collections.emptySet()
        }
    }

    /**
     * Collects and initializes the subclasses of the given class
     */
    @Deprecated("")
    fun <T> initializeSubtypeClasses(classRef: Class<T>) {
        getSubtypesOf(classRef).forEach {
            try {
                ReflectionUtils.initializeClass(it, getInstance())
            } catch (e: ExceptionInInitializerError) {
                logger.error("Failed to initialize a plugin class called '{}'", classRef.name, e)
            }
        }
    }
}
