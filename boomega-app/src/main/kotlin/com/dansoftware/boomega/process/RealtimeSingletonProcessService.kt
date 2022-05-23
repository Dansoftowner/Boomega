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

import com.dansoftware.boomega.util.isLocked
import com.google.gson.Gson
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
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

    private val randomAccessFile by lazy { RandomAccessFile(portFile.toFile(), "rw") }
    private val portFileChannel: FileChannel by lazy { randomAccessFile.channel }
    private var portFileLock: FileLock? = null

    override val port: Int
        get() {
            val foundPort: Int = try {
                if (isPortFileLocked()) Files.readString(portFile).toInt() else 0
            } catch (e: java.io.IOException) {
                0
            }
            return foundPort.also { if (it == 0) lockPortFile() }
        }

    init {
        createPortFileIfNotExists()
    }

    override fun serializeArguments(args: Array<String>): String {
        return gson.toJson(args)
    }

    override fun deserializeMessage(message: String): Array<String> {
        return gson.fromJson(message, Array<String>::class.java)
    }

    override fun persistPort(port: Int) {
        portFileChannel.write(ByteBuffer.wrap(port.toString().toByteArray()))
        portFileChannel.force(false)
    }

    override fun handleRequest(args: Array<String>) {
        ActivityLauncherImpl(args).launch()
    }

    override fun terminate() {
        exitProcess(0)
    }

    override fun release() {
        super.release()
        portFileChannel?.close()
        portFileLock?.release()
    }

    private fun createPortFileIfNotExists() {
        portFile.toFile().apply {
            parentFile.mkdirs()
            createNewFile()
        }
    }

    private fun lockPortFile() {
        portFileLock = portFileChannel.tryLock()
    }

    private fun isPortFileLocked(): Boolean {
        return try {
            portFileChannel.write(ByteBuffer.wrap("0".toByteArray()))
            false
        } catch (e: IOException) {
            true
        }
    }
}