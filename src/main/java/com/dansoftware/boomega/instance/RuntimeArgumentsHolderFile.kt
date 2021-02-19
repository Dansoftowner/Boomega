package com.dansoftware.boomega.instance

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Represents the file that holds the arguments temporarily that are passed to a new instance of the application.
 *
 * @author Daniel Gyorffy
 */
object RuntimeArgumentsHolderFile : File(FileUtils.getTempDirectory(), "libraryappruntimeparams.parms") {
    init {
        createNewFile()
    }

    fun clear() {
        try {
            BufferedWriter(FileWriter(this)).use { it.write(StringUtils.EMPTY) }
        } catch (ignored: IOException) {
        }
    }
}