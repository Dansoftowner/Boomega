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

package com.dansoftware.boomega.gui.google

import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import com.dansoftware.boomega.service.googlebooks.Volumes
import javafx.concurrent.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A [Task] used for loading Google Books represented as [Volumes].
 */
open class GoogleBooksSearchTask(private val query: GoogleBooksQuery) : Task<Volumes>() {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(GoogleBooksSearchTask::class.java)
    }

    override fun call(): Volumes {
        return query.build()
            .also { logger.debug("Google books query: {}", it) }
            .let {
                when {
                    it.isEmpty -> Volumes()
                    else -> it.load()
                }
            }
    }
}