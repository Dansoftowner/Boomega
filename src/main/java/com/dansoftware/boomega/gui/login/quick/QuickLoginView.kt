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

package com.dansoftware.boomega.gui.login.quick

import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.ContextTransformable
import com.dansoftware.boomega.gui.login.DatabaseLoginListener
import com.dansoftware.boomega.i18n.I18N
import com.dlsc.workbenchfx.SimpleHeaderView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.layout.Region

class QuickLoginView(databaseMeta: DatabaseMeta, loginListener: DatabaseLoginListener) : SimpleHeaderView<Region?>(
    "${I18N.getValue("login.quick.title")} - $databaseMeta", MaterialDesignIconView(MaterialDesignIcon.LOGIN)
), ContextTransformable {
    private val asContext: Context = Context.from(this)

    init {
        content = QuickForm(asContext, databaseMeta, loginListener)
    }

    override fun getContext(): Context {
        return asContext
    }
}