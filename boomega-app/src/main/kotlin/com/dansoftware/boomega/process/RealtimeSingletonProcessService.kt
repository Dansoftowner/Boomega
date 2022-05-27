/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.process

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class RealtimeSingletonProcessService @Inject constructor(
    @Named("portFile") private val portFile: Path
) : SocketBasedSingletonProcessService() {

    private val gson by lazy(::Gson)

    /**
     * An additional switch for controlling whether the port file should be read or not
     */
    private var shouldReadPortFile: Boolean = true

    override val port: Int
        get() {
            // if port file contained a wrong port number, but it couldn't be deleted
            if (shouldReadPortFile.not())
                return ZERO

            if (Files.exists(portFile))
                return Files.readString(portFile).toIntOrNull() ?: ZERO

            return ZERO
        }

    init {
        createPortFileIfNotExists()
    }

    override fun serializeArguments(args: Array<String>): String {
        return gson.toJson(args)
    }

    override fun deserializeMessage(message: String): Array<String> {
        return try {
            gson.fromJson(message, Array<String>::class.java) ?: throw RuntimeException()
        } catch (e: Exception) {
            emptyArray()
        }
    }

    override fun onPortWasUsedByAnotherApp(port: Int) {
        logger.debug("The port '{}' is used by a non-Boomega process", port)
        // the file contained a port number used by another app
        deletePortFile {
            logger.debug("Couldn't delete port file, singleton process mechanism failed!", it)
            shouldReadPortFile = false
        }
    }

    override fun persistPort(port: Int) {
        Files.writeString(portFile, port.toString())
    }

    override fun handleRequest(args: Array<String>) {
        ActivityLauncherImpl(args).launch()
    }

    override fun release(mainProcess: Boolean) {
        super.release(mainProcess)
        if (mainProcess) deletePortFile { logger.debug("Port file couldn't be deleted!", it) }
        else exitProcess(0)
    }

    private fun createPortFileIfNotExists() {
        portFile.toFile().apply {
            parentFile.mkdirs()
            createNewFile()
        }
    }

    private inline fun deletePortFile(onFailed: (Exception) -> Unit) {
        try {
            Files.delete(portFile)
        } catch (e: Exception) {
            onFailed(e)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RealtimeSingletonProcessService::class.java)

        private const val ZERO = 0
    }
}