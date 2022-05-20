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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset

abstract class SocketBasedSingletonProcessService : SingletonProcessService {

    @Volatile
    private var server: ServerSocket? = null

    protected abstract val port: Int
    protected open val charset: Charset = charset("UTF-8")

    /**
     *
     */
    protected abstract fun persistPort(port: Int)

    protected abstract fun handleRequest(args: Array<String>)

    protected abstract fun serializeArguments(args: Array<String>): String

    protected abstract fun deserializeMessage(message: String): Array<String>

    protected abstract fun terminate()

    protected open fun listeningThread(runnable: Runnable) = Thread(runnable).apply {
        name = "SingletonProcessService-Thread"
        isDaemon = true
    }

    override fun open(args: Array<String>) {
        try {
            val server = ServerSocket(port).also { this.server = it }
            persistPort(server.localPort)
            logger.debug("Created server-socket")
            listeningThread { startListeningProcedure(server) }.start()
            Runtime.getRuntime().addShutdownHook(Thread(::release))
        } catch (e: IOException) {
            logger.debug("Couldn't create server-socket", e)
            logger.debug("Assuming an application instance is already running")
            logger.debug("Sending arguments to the running instance...")
            notifyRunningProcess(args)
            terminate()
        }
    }

    override fun release() {
        logger.debug("Closing server-socket...")
        server?.close()
        server = null
    }

    private fun startListeningProcedure(server: ServerSocket) {
        while (true) {
            try {
                logger.debug("Listening to requests...")
                val connected: Socket = server.accept()

                logger.debug("Socket connected, handling request...")
                connected.getInputStream().bufferedReader(charset).use {
                    handleRequest(deserializeMessage(it.readText()))
                }
            } catch (e: IOException) {
                if (server.isClosed)
                    break
                logger.error("Couldn't accept socket-request", e)
            }
        }
        logger.debug("Listening procedure terminates.")
    }

    private fun notifyRunningProcess(args: Array<String>) {
        val client = Socket("localhost", port)
        client.getOutputStream().bufferedWriter(charset).use {
            it.write(serializeArguments(args))
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SingletonProcessService::class.java)
    }
}