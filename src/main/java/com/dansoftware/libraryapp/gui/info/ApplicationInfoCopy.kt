package com.dansoftware.libraryapp.gui.info

import com.dansoftware.libraryapp.util.os.OsInfo

/**
 * Returns the string that contains the valuable information about the application
 */
internal fun getApplicationInfoCopy(): String =
    """Version: ${System.getProperty("libraryapp.version")}
       |${System.getProperty("libraryapp.build.info")} 
       ------
       |OS: ${OsInfo.getName()}
       |OS Version: ${OsInfo.getVersion()}
       |OS Build: ${OsInfo.getBuildNumber()}
       ------
       |Java VM: ${System.getProperty("java.vm.name")}
       |Java VM version: ${System.getProperty("java.vm.version")}
       |Java VM vendor: ${System.getProperty("java.vm.vendor")}
       |Java version: ${System.getProperty("java.version")}
       |Java vendor: ${System.getProperty("java.vendor")}
       ------
       |JavaFX version: ${System.getProperty("javafx.version")}
       |JavaFX runtime version: ${System.getProperty("javafx.runtime.version")}
    """.trimMargin()