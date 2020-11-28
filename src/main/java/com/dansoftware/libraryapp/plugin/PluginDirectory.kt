package com.dansoftware.libraryapp.plugin

import org.apache.commons.io.FileUtils
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

    private val directory = File(System.getProperty("libraryapp.plugin.dir"))
    private val registrationFile = File(directory, "registered.conf")
    private val registeredFiles: MutableList<String> = ArrayList(registrationFile.readLines())

    init {
        createDirectory()
        createRegFile()
    }

    /**
     * Creates the plugin directory if not exists
     */
    private fun createDirectory(): Boolean = when {
        directory.exists().not() -> directory.mkdirs()
        else -> true
    }

    private fun createRegFile(): Boolean = when {
        registrationFile.exists().not() -> registrationFile.createNewFile()
        else -> true
    }

    private fun registerPlugin(file: File) {
        registeredFiles.add(file.name)
        registrationFile.writeText(registeredFiles.joinToString("\n"))
    }

    /**
     * Lists the plugin archives as [File] objects
     */
    fun getPluginFiles(): Array<File>? {
        val extensionPattern = Pattern.compile(".*\\.jar", Pattern.CASE_INSENSITIVE)
        return directory.listFiles { _, name -> extensionPattern.matcher(name).matches() && registeredFiles.contains(name) }
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
        Files.copy(src.toPath(), File(directory, src.name).toPath())
        registerPlugin(src)
    }

    fun clear() {
        val newRegistrationFileContent = ArrayList<String>()
        registrationFile.reader().forEachLine {
            if (File(directory, it).exists()) {
                newRegistrationFileContent.add(it)
            }
        }
        getPluginFiles()?.forEach {
            if (newRegistrationFileContent.contains(it.name).not()) {
                try {
                    Files.deleteIfExists(it.toPath())
                } catch (e: IOException) {
                }
            }
        }
    }

    fun isEmpty(): Boolean = directory.isDirectory && directory.listFiles { file -> file.isDirectory.not() }?.isEmpty() ?: false
}