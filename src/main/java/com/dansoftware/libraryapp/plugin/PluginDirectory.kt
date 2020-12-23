package com.dansoftware.libraryapp.plugin

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

    private val directory = File(System.getProperty("libraryapp.plugin.dir"))
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
    fun getPluginFiles(): Array<File>? {
        return directory.listFiles { _, name ->
            JAR_EXTENSION_PATTERN.matcher(name).matches() && registeredFiles.contains(name)
        }
    }

    /**
     * Lists the plugin archives as [URL] objects
     */
    fun getPluginFilesAsUrls(): Array<URL>? {
        return try {
            FileUtils.toURLs(getPluginFiles())
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