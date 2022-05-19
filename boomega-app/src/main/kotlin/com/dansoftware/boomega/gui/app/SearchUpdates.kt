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

package com.dansoftware.boomega.gui.app

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.update.LAST_UPDATE_SEARCH
import com.dansoftware.boomega.update.Release
import com.dansoftware.boomega.update.SEARCH_UPDATES
import com.dansoftware.boomega.update.UpdateSearcher
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

private val logger = LoggerFactory.getLogger("com.dansoftware.boomega.gui.app.SearchUpdatesKt")

/**
 * Searches for updates if necessary. Also saves the date-time for the update-search.
 */
fun BoomegaApp.searchForUpdates(): Release? {
    val preferences = get(Preferences::class)
    return when {
        preferences[SEARCH_UPDATES] -> {
            notifyPreloader("preloader.update.search")
            val updateSearcher = get(UpdateSearcher::class)
            preferences.editor[LAST_UPDATE_SEARCH] = LocalDateTime.now()
            updateSearcher.trySearch { e -> logger.error("Couldn't search for updates", e) }
        }
        else -> null
    }
}