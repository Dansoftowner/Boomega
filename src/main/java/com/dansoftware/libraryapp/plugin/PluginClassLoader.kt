package com.dansoftware.libraryapp.plugin

import java.net.URLClassLoader

/**
 * The [PluginClassLoader] can load classes from both the class-path and the third-party plugins located
 * in the plugin-directory.
 *
 * @author Daniel Gyorffy
 */
object PluginClassLoader : URLClassLoader(PluginDirectory().pluginFileURLS, getSystemClassLoader()) {
    @JvmStatic
    fun getInstance(): PluginClassLoader = this
}