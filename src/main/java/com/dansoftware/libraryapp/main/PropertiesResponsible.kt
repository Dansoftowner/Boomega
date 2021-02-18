package com.dansoftware.libraryapp.main

import com.dansoftware.libraryapp.util.os.OsInfo
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import javax.swing.filechooser.FileSystemView

/**
 * Responsible for adding the necessary system properties that are needed for the application.
 *
 * @author Daniel Gyorffy
 */
object PropertiesResponsible {

    /* ****** Keys ****** */

    private const val LIBRARY_APP_VERSION = "libraryapp.version"
    private const val LIBRARY_APP_BUILD_INFO = "libraryapp.build.info"
    private const val LIBRARY_APP_FILE_EXTENSION = "libraryapp.file.extension"
    private const val LOG_FILE_PATH = "log.file.path"
    private const val LOG_FILE_FULL_PATH = "log.file.path.full"
    private const val PLUGIN_DIRECTORY_PATH = "libraryapp.plugin.dir"
    private const val CONFIG_FILE_PATH = "libraryapp.config.file.path"
    private const val DEFAULT_DIRECTORY_PATH = "libraryapp.dir.default.path"

    /* **** VALUES **** */

    /**
     * the app's version
     */
    private const val LIBRARY_APP_VERSION_VALUE = "0.0.0"

    /**
     * some info of this build
     */
    private const val LIBRARY_APP_BUILD_INFO_VALUE = "Built on 2020-09-18"

    /**
     * the libraryapp database file-extension
     */
    private const val LIBRARY_APP_FILE_EXTENSION_VALUE = "bmdb"

    /**
     * The name of the default Boomega documents folder
     */
    private const val BOOMEGA_DOCUMENTS_FOLDER = "BoomegaDocuments"

    /**
     * The log-file's path without the extension
     */
    private val LOG_FILE_PATH_VALUE = FileUtils.getFile(FileUtils.getTempDirectory(), "libraryapp").toString()

    /**
     * The log-file's path with the extension
     */
    private val LOG_FILE_FULL_PATH_VALUE: String =
        listOf(LOG_FILE_PATH_VALUE, "log").joinToString(FilenameUtils.EXTENSION_SEPARATOR_STR)

    /**
     * Puts all the necessary system-properties that the application needs.
     */
    @JvmStatic
    fun setupSystemProperties() {
        putLogFileProperties()
        putJFXProperties()
        putAppSpecificProperties()
    }

    /**
     * Puts the log file information into the system properties
     */
    private fun putLogFileProperties() {
        System.setProperty(LOG_FILE_PATH, LOG_FILE_PATH_VALUE)
        System.setProperty(LOG_FILE_FULL_PATH, LOG_FILE_FULL_PATH_VALUE)
    }

    /**
     * Puts some javaFX information into the system properties
     */
    private fun putJFXProperties() = com.sun.javafx.runtime.VersionInfo.setupSystemProperties()

    /**
     * Puts the libraryapp-specific values into the system properties
     */
    private fun putAppSpecificProperties() {
        System.setProperty(LIBRARY_APP_VERSION, LIBRARY_APP_VERSION_VALUE)
        System.setProperty(LIBRARY_APP_BUILD_INFO, LIBRARY_APP_BUILD_INFO_VALUE)
        System.setProperty(LIBRARY_APP_FILE_EXTENSION, LIBRARY_APP_FILE_EXTENSION_VALUE)
        System.setProperty(PLUGIN_DIRECTORY_PATH, getPluginDirPath())
        System.setProperty(CONFIG_FILE_PATH, getConfigFilePath())
        System.setProperty(DEFAULT_DIRECTORY_PATH, getDefaultDirectoryFilePath());
    }

    private fun getDefaultDirectoryFilePath() =
        File(FileSystemView.getFileSystemView().defaultDirectory, BOOMEGA_DOCUMENTS_FOLDER).absolutePath

    /**
     * Returns the config file's path
     */
    private fun getConfigFilePath() =
        listOf(FileUtils.getUserDirectoryPath(), ".libraryapp2020", "bmcfg").joinToString(File.separator)

    /**
     * Returns the plugin directory's path
     */
    private fun getPluginDirPath(): String = when {
        OsInfo.isWindows() -> {
            var rootDir: String = System.getenv("APPDATA")
            if (StringUtils.isBlank(rootDir)) {
                rootDir = FileUtils.getUserDirectoryPath()
            }
            File(
                File(rootDir),
                listOf("Dansoftware", "boomega", "plugin").joinToString(File.separator)
            ).absolutePath
        }
        OsInfo.isLinux() ->
            listOf(
                FileUtils.getUserDirectoryPath(),
                ".libraryapp2020",
                "profiles",
                "plugin"
            ).joinToString(File.separator)
        OsInfo.isMac() ->
            listOf("~", "Library", "Application", "Support", "Dansoftware", "libraryapp2020", "plugin").joinToString(
                File.separator
            )
        else -> "plugin"
    }
}