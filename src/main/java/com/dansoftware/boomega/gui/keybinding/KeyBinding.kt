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

package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.util.os.OsInfo
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent

class KeyBinding(
    val id: String,
    val i18nTitle: String,
    val i18nDescription: String,
    val defaultKeyCombination: KeyCombination
) {

    val keyCombinationProperty: ObjectProperty<KeyCombination> =
        SimpleObjectProperty(defaultKeyCombination)
    val keyCombination: KeyCombination
        get() = keyCombinationProperty.get()

    constructor(
        id: String,
        i18nTitle: String,
        i18nDescription: String,
        winLinuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination
    ) : this(
        id,
        i18nTitle,
        i18nDescription,
        when {
            OsInfo.isMac() -> macKeyCombination
            else -> winLinuxKeyCombination
        }
    )

    constructor(
        id: String,
        i18nTitle: String,
        i18nDescription: String,
        winKeyCombination: KeyCombination,
        linuxKeyCombination: KeyCombination,
        macKeyCombination: KeyCombination,
    ) : this(
        id,
        i18nTitle,
        i18nDescription,
        when {
            OsInfo.isLinux() -> linuxKeyCombination
            OsInfo.isMac() -> macKeyCombination
            else -> winKeyCombination
        }
    )

    fun match(keyEvent: KeyEvent) = keyCombination.match(keyEvent)
}