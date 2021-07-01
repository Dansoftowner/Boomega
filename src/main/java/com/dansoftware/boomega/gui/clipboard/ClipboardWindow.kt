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

package com.dansoftware.boomega.gui.clipboard

import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.stage.Window

class ClipboardWindow(owner: Window?, view: ClipboardView) : BaseWindow<ClipboardView>(I18N.getValue("window.clipboard.title"), view) {
    init {
        width = 1200.0
        initOwner(owner)
        setOnCloseRequest { view.releaseListeners() }
    }
}