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

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.keybinding.KeyBinding

open class Action(
    val name: String,
    val iconStyleClass: String,
    val keyBinding: KeyBinding?,
    private val operation: (Context) -> Unit = { _ -> }
) {

    open val isDisabled get() = false

    constructor(
        name: String,
        iconStyleClass: String,
        operation: (Context) -> Unit = { _ -> }
    ) : this(name, iconStyleClass, null, operation)

    open operator fun invoke(context: Context) {
        operation(context)
    }
}