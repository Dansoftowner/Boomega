/*
 * Boomega
 * Copyright (C)  $originalComment.match("Copyright (\d+)", 1, "-")2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.gui.keybinding.KeyBinding
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Tooltip

class KeyBindingTooltip(text: String, keyBinding: KeyBinding) : Tooltip() {
    init {
        textProperty().bind(
            SimpleStringProperty("$text (")
                .concat(keyBinding.keyCombinationProperty)
                .concat(")")
        )
    }
}