package com.dansoftware.libraryapp.plugin

import com.dansoftware.libraryapp.util.ReflectionUtils
import org.slf4j.LoggerFactory
import java.net.URLClassLoader
import java.util.*

/**
 * The [PluginClassLoader] can load classes from both the class-path and the third-party plugins located
 * in the plugin-directory.
 *
 * @author Daniel Gyorffy
 */
object PluginClassLoader : URLClassLoader(PluginDirectory.getPluginFilesAsUrls(), getSystemClassLoader()) {

    @JvmStatic
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * For already existing code bases
     */
    @JvmStatic
    fun getInstance(): PluginClassLoader = this

    fun isEmpty(): Boolean = PluginDirectory.isEmpty()

    fun getReadPluginsCount() = this.urLs.size

    override fun close() {
        super.close()
        PluginDirectory.clear()
    }

    /**
     * Collects all subtypes of the given class
     */
    @Suppress("UNCHECKED_CAST")
    fun <T, S : T> getSubtypesOf(classRef: Class<T>): Set<Class<S>> {
        return when {
            this.isEmpty().not() -> ReflectionUtils.getSubtypesOf(classRef, this) as Set<Class<S>>
            else -> Collections.emptySet()
        }
    }

    /**
     * Collects and initializes the subclasses of the given class
     */
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
