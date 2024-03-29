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

package com.dansoftware.boomega.gui.action

import com.dansoftware.boomega.gui.action.api.Action
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.app.ApplicationRestart
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.i18n.api.i18n
import javafx.scene.control.ButtonType

object RestartApplicationAction : Action(
    i18n("action.restart"),
    "update-icon",
    KeyBindings.restartApplication
)  {

    private val dialogShownContexts: MutableSet<Context> = HashSet()

    override fun invoke(context: Context) {
        if (!dialogShownContexts.contains(context)) {
            context.showConfirmationDialog(
                I18N.getValue("app.restart.dialog.title"),
                I18N.getValue("app.restart.dialog.msg")
            ) {
                when {
                    it.typeEquals(ButtonType.YES) -> ApplicationRestart.restart()
                }
                dialogShownContexts.remove(context)
            }
            dialogShownContexts.add(context)
        }
    }
}