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

package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.stage.Modality
import javafx.stage.Window

class PreferencesWindow(preferences: Preferences, owner: Window?) :
    BaseWindow<PreferencesView>(I18N.getValue("window.preferences.title"), PreferencesView(preferences)) {

    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        width = 800.0
        height = 500.0
    }
}