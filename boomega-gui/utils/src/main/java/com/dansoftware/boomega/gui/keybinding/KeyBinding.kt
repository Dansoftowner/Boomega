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

package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.util.os.OsInfo
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent

class KeyBinding(
    val id: String,
    val title: String,
    val description: () -> String,
    val defaultKeyCombination: KeyCombination
) {

    val keyCombinationProperty: ObjectProperty<KeyCombination> =
        SimpleObjectProperty(defaultKeyCombination)
    val keyCombination: KeyCombination
        get() = keyCombinationProperty.get()

    constructor(
        id: String,
        title: String,
        description: () -> String,
        winLinuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination
    ) : this(
        id,
        title,
        description,
        when {
            OsInfo.isMac() -> macKeyCombination
            else -> winLinuxKeyCombination
        }
    )

    constructor(
        id: String,
        title: String,
        description: () -> String,
        winKeyCombination: KeyCombination,
        linuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination
    ) : this(
        id,
        title,
        description,
        when {
            OsInfo.isLinux() -> linuxKeyCombination
            OsInfo.isMac() -> macKeyCombination
            else -> winKeyCombination
        }
    )

    fun match(keyEvent: KeyEvent) = keyCombination.match(keyEvent)
}