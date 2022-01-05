package com.dansoftware.boomega.gui.info

import com.dansoftware.boomega.util.os.OsInfo

/**
 * Returns the string that contains the valuable information about the application
 */
internal fun getApplicationInfoCopy(): String =
    """Version: ${System.getProperty("boomega.version")}
       |${System.getProperty("boomega.build.info")} 
       ------
       |OS: ${OsInfo.name}
       |OS Version: ${OsInfo.version}
       |OS Build: ${OsInfo.buildNumber}
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