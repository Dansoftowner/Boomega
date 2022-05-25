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

import org.jetbrains.annotations.MustBeInvokedByOverriders
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset

/**
 * A socket based [SingletonProcessService] prevents multiple application instances by
 * opening a socket for this purpose, and listening to new requests.
 */
abstract class SocketBasedSingletonProcessService : SingletonProcessService {

    @Volatile
    private var server: ServerSocket? = null

    /**
     * The port should the service run on.
     * - Should be `0` if there is need to search an open port.
     * - Or it should be the port persisted earlier (see [persistPort])
     */
    protected abstract val port: Int

    /**
     * The charset the sockets should use for communication.
     */
    protected open val charset: Charset = charset("UTF-8")

    /**
     * Should persist the port used for running the service on.
     * It only has a role if the port is determined dynamically.
     */
    protected abstract fun persistPort(port: Int)

    /**
     * Called when a new process notifies the already running application.
     *
     * @param args the application arguments the external process received
     */
    protected abstract fun handleRequest(args: Array<String>)

    /**
     * Defines how to terminate the current process if there is an already running application.
     */
    protected abstract fun terminate()

    /**
     * Serializes the application arguments to be sent to the already running application process.
     */
    protected abstract fun serializeArguments(args: Array<String>): String

    /**
     * Deserializes the serialized message (application arguments) received from an external process.
     */
    protected abstract fun deserializeMessage(message: String): Array<String>

    /**
     * Executed when the given [port] is used by another application
     */
    protected abstract fun onPortWasUsedByAnotherApp()

    /**
     * Creates the thread object that should run the listening procedure in the background.
     */
    protected open fun listeningThread(runnable: Runnable) = Thread(runnable).apply {
        name = "SingletonProcessService-Thread"
        isDaemon = true
    }

    override fun open(args: Array<String>) {
        try {
            val port = this.port // local cache
            val server = ServerSocket(port).also { this.server = it }
            if (port == 0) // the port was detected dynamically
                persistPort(server.localPort)
            logger.debug("Created server-socket")
            listeningThread { startListeningProcedure(server) }.start()
            Runtime.getRuntime().addShutdownHook(Thread(::release))
        } catch (e: IOException) {
            logger.debug("Couldn't create server-socket", e)
            logger.debug("Assuming an application instance is already running")
            logger.debug("Sending arguments to the running instance...")
            if (!notifyRunningProcess(args)) {
                onPortWasUsedByAnotherApp()
                open(args)
                return
            }
            terminate()
        }
    }


    final override fun release() {
        release(true)
    }

    @MustBeInvokedByOverriders
    open fun release(value: Boolean) { // TODO: more appropriate name
        if (value) {
            logger.debug("Closing server-socket...")
            server?.close()
            server = null
        }
    }

    /**
     * Starts the listening procedure, but not on a background thread (so it has to be wrapped).
     */
    private fun startListeningProcedure(server: ServerSocket) {
        while (true) {
            try {
                logger.debug("Listening to requests...")
                val connected: Socket = server.accept()

                logger.debug("Socket connected, handling request...")
                handleRequest(deserializeMessage(connected.readMessage()))
                connected.sendMessage(BOOMEGA_REPLY_MSG)
            } catch (e: IOException) {
                if (server.isClosed)
                    break
                logger.error("Couldn't accept socket-request", e)
            }
        }
        logger.debug("Listening procedure terminates.")
    }

    /**
     * Notifies the already running process and sends the arguments to it.
     */
    private fun notifyRunningProcess(args: Array<String>): Boolean {
        val client = Socket("localhost", port)
        client.sendMessage(serializeArguments(args))
        return client.readMessage() == BOOMEGA_REPLY_MSG
    }

    /**
     * Sends the string message to the socket receiver
     */
    private fun Socket.sendMessage(msg: String) {
        getOutputStream().bufferedWriter(charset).use {
            it.write(msg)
        }
    }

    /**
     * Reads the string message the socket sent
     */
    private fun Socket.readMessage(): String {
        return getInputStream().bufferedReader(charset).use { it.readText() }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SingletonProcessService::class.java)

        private const val BOOMEGA_REPLY_MSG = "BOOMEGA_REPLY_MESSAGE"
    }
}