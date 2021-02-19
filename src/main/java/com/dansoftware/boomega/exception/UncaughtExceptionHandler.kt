package com.dansoftware.boomega.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UncaughtExceptionHandler::class.java)
    }

    override fun uncaughtException(t: Thread, e: Throwable) = logger.error("Uncaught exception occurred", e)
}