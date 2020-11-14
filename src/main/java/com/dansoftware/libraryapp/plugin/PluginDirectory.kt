package com.dansoftware.libraryapp.plugin

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.regex.Pattern

/**
 * Represents the directory that holds the plugin jar files on the disk.
 *
 * @author Daniel Gyorffy
 */
object PluginDirectory {

    private val directory = File(System.getProperty("libraryapp.plugin.dir"))

    /**
     * Creates the plugin directory if not exists
     */
    private fun createIfNotExists(): Boolean = when {
        directory.exists().not() -> directory.mkdirs()
        else -> true
    }

    /**
     * Lists the plugin archives as [File] objects
     */
    fun getPluginFiles(): Array<File>? {
        createIfNotExists()
        val extensionPattern = Pattern.compile(".*\\.jar", Pattern.CASE_INSENSITIVE)
        return directory.listFiles { _, name -> extensionPattern.matcher(name).matches() }
    }

    /**
     * Lists the plugin archives as [URL] objects
     */
    fun getPluginFilesAsUrls(): Array<URL>? {
        createIfNotExists()
        return try {
            FileUtils.toURLs(getPluginFiles())
        } catch (e: IOException) {
            null
        }
    }
}