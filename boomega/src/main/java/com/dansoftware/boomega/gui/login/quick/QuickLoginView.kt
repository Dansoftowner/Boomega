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

package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.base.BaseView
import com.dansoftware.boomega.gui.login.DatabaseLoginListener
import javafx.scene.layout.VBox

class QuickLoginView(databaseMeta: DatabaseMeta, loginListener: DatabaseLoginListener) : BaseView() {

    init {
        content = VBox(
            QuickLoginToolbar(databaseMeta),
            QuickForm(this, databaseMeta, loginListener)
        )
    }
}