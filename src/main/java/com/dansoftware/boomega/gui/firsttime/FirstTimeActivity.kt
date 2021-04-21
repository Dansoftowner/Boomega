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

package com.dansoftware.boomega.gui.firsttime

import com.dansoftware.boomega.config.Preferences
import java.util.*

/**
 * Used for showing [FirstTimeDialog] with a [FirstTimeDialogWindow].
 *
 * @author Daniel Gyorffy
 */
class FirstTimeActivity(private val preferences: Preferences) {
    init {
        Objects.requireNonNull(preferences, "Preferences shouldn't be null")
    }

    fun show() {
        val window = FirstTimeDialogWindow(FirstTimeDialog(preferences))
        window.showAndWait()
    }

    companion object {
        @JvmStatic
        fun isNeeded(preferences: Preferences): Boolean = preferences.source.isCreated
    }
}