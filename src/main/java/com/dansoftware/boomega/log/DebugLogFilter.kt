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

package com.dansoftware.boomega.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

/**
 * Filters debug logs out if the system property `accept.debug.logs` is not _true_.
 */
class DebugLogFilter : Filter<ILoggingEvent>() {

    private val acceptDebugLogs by lazy {
        System.getProperty("accept.debug.logs").toBoolean()
    }

    override fun decide(event: ILoggingEvent): FilterReply {
        return when (event.level) {
            Level.DEBUG ->
                if (acceptDebugLogs) FilterReply.ACCEPT else FilterReply.DENY
            else -> FilterReply.NEUTRAL
        }
    }
}