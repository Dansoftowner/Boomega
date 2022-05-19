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

import com.dansoftware.boomega.gui.preloader.BoomegaPreloader
import com.dansoftware.boomega.i18n.api.i18n

/**
 * Handles the application arguments received by the program.
 * It means:
 * - Showing a message on the preloader
 * - Showing a notification on the launched UI entry
 */
fun BoomegaApp.handleApplicationArgument() {
    // Showing message on the preloader about the launched database
    launchedDatabase?.let {
        notifyPreloader(
            BoomegaPreloader.MessageNotification(
                message = i18n("preloader.file.open", it.name),
                priority = BoomegaPreloader.MessageNotification.Priority.HIGH
            )
        )
    }

    // Showing a notification after the application starts about the launched database
    postLaunch { context, launched ->
        launched?.let {
            context.showInformationNotification(
                title = i18n("database.file.launched", launched.name),
                message = null
            )
        }
    }
}