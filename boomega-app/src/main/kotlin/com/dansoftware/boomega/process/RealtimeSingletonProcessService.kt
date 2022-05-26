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

    private var shouldReadPortFile: Boolean = true

    override val port: Int
        get() = if (shouldReadPortFile && Files.exists(portFile)) Files.readString(portFile).toIntOrNull() ?: 0 else 0

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

    override fun onPortWasUsedByAnotherApp() {
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

    override fun terminate() {
        exitProcess(0)
    }

    override fun release(value: Boolean) {
        super.release()
        if (value) deletePortFile { logger.debug("Port file couldn't be deleted!", it) }
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
    }
}