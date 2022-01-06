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

package com.dansoftware.boomega.gui.action

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.i18n.I18N

open class Action(
    private val i18nName: String,
    val iconStyleClass: String,
    val keyBinding: KeyBinding?,
    private val operation: (Context, Preferences, DatabaseTracker) -> Unit = { _, _, _ -> }
) {

    private var displayNameBacking: String? = null

    val displayName: String
        get() = displayNameBacking ?: I18N.getValue(i18nName).also { displayNameBacking = it }

    constructor(
        i18nName: String,
        iconStyleClass: String,
        operation: (Context, Preferences, DatabaseTracker) -> Unit = { _, _, _ -> }
    ) : this(i18nName, iconStyleClass, null, operation)

    open fun invoke(context: Context, preferences: Preferences, databaseTracker: DatabaseTracker) {
        operation(context, preferences, databaseTracker)
    }
}