/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.action.impl

import com.dansoftware.boomega.config.LAST_UPDATE_SEARCH
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService
import com.dansoftware.boomega.gui.action.Action
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.util.onFailed
import com.dansoftware.boomega.gui.util.onRunning
import com.dansoftware.boomega.gui.util.onSucceeded
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.update.Release
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.concurrent.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

object SearchForUpdatesAction : Action(
    "action.update_search",
    "update-icon"
) {

    private val logger: Logger = LoggerFactory.getLogger(SearchForUpdatesAction::class.java)

    override fun invoke(context: Context, preferences: Preferences, databaseTracker: DatabaseTracker) {
        preferences.editor[LAST_UPDATE_SEARCH] = LocalDateTime.now()
        CachedExecutor.submit(object : Task<Release?>() {
            init {
                onRunning {
                    context.showIndeterminateProgress()
                }
                onFailed {
                    context.stopProgress()
                    context.showErrorDialog(
                        I18N.getValue("update.failed.title"),
                        I18N.getValue("update.failed.msg"),
                        it as? Exception
                    ) {}
                    logger.error("Update search failed", it)
                }
                onSucceeded { githubRelease ->
                    context.stopProgress()
                    githubRelease?.let { UpdateActivity(context, it).show() }
                        ?: context.showInformationDialog(
                            I18N.getValue("update.up_to_date.title"),
                            I18N.getValue("update.up_to_date.msg")
                        ) {}
                }
            }

            override fun call() =
                DIService[UpdateSearcher::class.java].search()
        })
    }
}