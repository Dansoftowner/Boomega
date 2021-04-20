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

package com.dansoftware.boomega.gui.updatedialog.segment.download

import com.dansoftware.boomega.update.DownloadableBinary
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox

class PackageChooserArea : VBox(10.0) {

    private val toggleGroup = ToggleGroup()
    private val selectedBinary: ObjectProperty<DownloadableBinary> =
        SimpleObjectProperty<DownloadableBinary>().apply {
            toggleGroup.selectedToggleProperty().addListener { _, _, value ->
                set((value as BinaryRadioButton).binary)
            }
        }

    val isEmpty: Boolean
        get() = children.isEmpty()


    fun getSelectedBinary() = selectedBinary.get()
    fun selectedBinaryProperty() = selectedBinary

    fun putEntry(binary: DownloadableBinary) {
        children.add(BinaryRadioButton(binary, toggleGroup))
    }

    private class BinaryRadioButton(val binary: DownloadableBinary, toggleGroup: ToggleGroup) :
        RadioButton(binary.name) {
        init {
            this.toggleGroup = toggleGroup
        }
    }
}