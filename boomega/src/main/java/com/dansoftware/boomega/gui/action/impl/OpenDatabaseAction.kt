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

package com.dansoftware.boomega.gui.action.impl

import com.dansoftware.boomega.database.bmdb.gui.BMDBDatabaseOpener
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.action.Action
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.launch.LauncherMode
import com.dansoftware.boomega.gui.launch.activityLauncher
import com.dansoftware.boomega.gui.util.submitFXTask
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.toKFunction
import java.util.concurrent.ExecutorService

object OpenDatabaseAction : Action(
    i18n("action.open_database"),
    "file-icon",
    KeyBindings.openDatabase
) {

    override fun invoke(context: Context) {
        BMDBDatabaseOpener().showOpenDialog(context.contextWindow)?.also {
            // launches the database
            get(ExecutorService::class, "cachedExecutor").submitFXTask(
                context,
                activityLauncher(
                    LauncherMode.INTERNAL,
                    initialDatabase = it
                ).toKFunction()
            )
        }
    }
}