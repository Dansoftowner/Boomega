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
import com.dansoftware.boomega.gui.firsttime.FirstTimeActivity
import com.dansoftware.boomega.util.concurrent.notify
import com.dansoftware.boomega.util.concurrent.wait
import javafx.application.Platform
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.dansoftware.boomega.gui.app.ShowSetupWizardKt")

/**
 * Shows the [FirstTimeActivity] if needed and hides/resumes the Preloader when necessary.
 *
 * @return `true` if the first time dialog was shown; `false` otherwise
 */
fun BoomegaApp.showSetupWizard(): Boolean {
    val lock = Any()
    return synchronized(lock) {
        when {
            FirstTimeActivity.isNeeded(get(Preferences::class)) -> {
                hidePreloader()
                logger.debug("First time dialog is needed")
                Platform.runLater {
                    synchronized(lock) {
                        FirstTimeActivity(get(Preferences::class)).show()
                        lock.notify()
                    }
                }
                // waiting till the first time dialog completes
                lock.wait()
                showPreloader()
                true
            }
            else -> false
        }
    }
}