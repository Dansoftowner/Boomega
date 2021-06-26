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

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.gui.util.asKeyCombination
import com.dansoftware.boomega.gui.util.isUndefined
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.Event
import javafx.scene.control.TextField
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import org.apache.commons.lang3.StringUtils

class KeyBindDetectionField(initial: KeyCombination) : TextField() {

    private val keyCombination: ObjectProperty<KeyCombination> = object : SimpleObjectProperty<KeyCombination>() {
        override fun invalidated() {
            this@KeyBindDetectionField.text = StringUtils.getIfEmpty(get().displayText, null)
        }
    }

    init {
        this.isEditable = false
        //TODO: tooltip
        this.keyCombination.set(initial)
        this.setOnContextMenuRequested(Event::consume)
        this.setOnKeyTyped(KeyEvent::consume)
        this.setOnKeyPressed { event ->
            event.consume()
            when {
                event.isUndefined().not() -> {
                    event.asKeyCombination()?.let {
                        keyCombination.set(it)
                    }
                }
            }
        }
    }

    fun keyCombinationProperty() = keyCombination
}