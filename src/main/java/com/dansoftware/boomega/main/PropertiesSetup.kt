package com.dansoftware.boomega.main

import com.dansoftware.boomega.util.CommonDirectories
import com.dansoftware.boomega.util.os.OsInfo
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File

/**
 * Responsible for adding the necessary system properties that are needed for the application.
 *
 * @author Daniel Gyorffy
 */
object PropertiesSetup {

    private const val APP_NAME = "app.name"
    private const val BOOMEGA_VERSION = "boomega.version"
    private const val BOOMEGA_BUILD_INFO = "boomega.build.info"
    private const val BOOMEGA_FILE_EXTENSION = "boomega.file.extension"
    private const val LOG_FILE_PATH = "log.file.path"
    private const val LOG_FILE_FULL_PATH = "log.file.path.full"
    private const val PLUGIN_DIRECTORY_PATH = "boomega.plugin.dir"
    private const val CONFIG_FILE_PATH = "boomega.config.file.path"
    private const val DEFAULT_DIRECTORY_PATH = "boomega.dir.default.path"

    private const val APP_NAME_VALUE = "Boomega"
    private const val BOOMEGA_VERSION_VALUE = "0.6.51"
    private const val BOOMEGA_BUILD_INFO_VALUE = "Built on 2021 May 19th"
    private const val BOOMEGA_FILE_EXTENSION_VALUE = "bmdb"
    private const val BOOMEGA_DOCUMENTS_FOLDER = "BoomegaDocuments"

    /**
     * The log-file's path without the extension (for the logback configuration)
     */
    private val LOG_FILE_PATH_VALUE = FileUtils.getFile(FileUtils.getTempDirectory(), "boomega").toString()

    /**
     * The log-file's path with the extension
     */
    private val LOG_FILE_FULL_PATH_VALUE: String = "$LOG_FILE_PATH_VALUE.log"

    /**
     * Puts all the necessary system-properties that the application needs.
     */
    @JvmStatic
    fun setupSystemProperties() {
        adjustJavaLibraryPath()
        putLogFileProperties()
        putJFXProperties()
        putAppSpecificProperties()
    }

    /**
     * Adjusts the *java library path* by prefixing it with the current java runtimes bin folder
     * to make sure that later the JavaFX platform loads the native *.dll* libraries from the right
     * place. **Windows only**. It has meaning only if the program runs as a native app.
     * See [**#146**](https://github.com/Dansoftowner/Boomega/issues/146).
     */
    private fun adjustJavaLibraryPath() {
        if (OsInfo.isWindows()) {
            val javaLibraryPath = System.getProperty("java.library.path")
            val javaRuntime = System.getProperty("java.home")

            System.setProperty(
                "java.library.path",
                "$javaRuntime\\bin;$javaLibraryPath"
            )
        }
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
        System.setProperty(APP_NAME, APP_NAME_VALUE)
        System.setProperty(BOOMEGA_VERSION, BOOMEGA_VERSION_VALUE)
        System.setProperty(BOOMEGA_BUILD_INFO, BOOMEGA_BUILD_INFO_VALUE)
        System.setProperty(BOOMEGA_FILE_EXTENSION, BOOMEGA_FILE_EXTENSION_VALUE)
        System.setProperty(PLUGIN_DIRECTORY_PATH, getPluginDirPath())
        System.setProperty(CONFIG_FILE_PATH, getConfigFilePath())
        System.setProperty(DEFAULT_DIRECTORY_PATH, getDefaultDirectoryFilePath())
    }

    private fun getDefaultDirectoryFilePath() =
        File(CommonDirectories.documentsDir, BOOMEGA_DOCUMENTS_FOLDER).absolutePath

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
            var appdata: String = System.getenv("APPDATA")
            if (StringUtils.isBlank(appdata)) {
                appdata = FileUtils.getUserDirectoryPath()
            }
            File(
                File(appdata),
                listOf("Dansoftware", "boomega", "plugin").joinToString(File.separator)
            ).absolutePath
        }
        OsInfo.isLinux() ->
            listOf(
                FileUtils.getUserDirectoryPath(),
                "boomega",
                "plugin"
            ).joinToString(File.separator)
        OsInfo.isMac() ->
            listOf(FileUtils.getUserDirectoryPath(), "boomega", "plugin").joinToString(File.separator)
        else -> "plugin"
    }
}