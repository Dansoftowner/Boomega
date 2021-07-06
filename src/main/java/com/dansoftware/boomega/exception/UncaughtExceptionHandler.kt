/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.exception

import javafx.application.Platform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UncaughtExceptionHandler::class.java)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        logger.error("Uncaught exception occurred", e)
        showGuiMessage(e)
    }

    private fun showGuiMessage(e: Throwable) {
        try {
            Platform.runLater {
                UncaughtExceptionDialog(e).show()
            }
        } catch (illegalStateException: IllegalStateException) {
            Platform.startup {
                UncaughtExceptionDialog(e).show()
            }
        }
    }
}
