package com.dansoftware.libraryapp.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        LOGGER.error("Uncaught exception occurred", e)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(UncaughtExceptionHandler::class.java)
    }
}