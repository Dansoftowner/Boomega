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

import com.dansoftware.boomega.gui.action.Action
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.i18n
import javafx.stage.Stage

object FullScreenAction : Action(
    i18n("action.full_screen"),
    "full-screen-icon",
    KeyBindings.fullScreen
) {
    override fun invoke(context: Context) {
        context.contextWindow.also { if (it is Stage) it.isFullScreen = it.isFullScreen.not() }
    }
}