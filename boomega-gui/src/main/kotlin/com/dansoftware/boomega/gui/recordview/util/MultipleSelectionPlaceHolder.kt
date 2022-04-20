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

package com.dansoftware.boomega.gui.recordview.util

import com.dansoftware.boomega.gui.util.asCentered
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

class MultipleSelectionPlaceHolder : StackPane() {

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add(JMetroStyleClass.BACKGROUND)
        styleClass.add("multiple-selection-place-holder")
        buildUI()
    }

    private fun buildUI() {
        children.add(buildGroup())
    }

    private fun buildGroup() = Group(buildHBox())

    private fun buildHBox() =
        HBox(5.0,
            icon("buffer-icon").asCentered(),
            Label(i18n("google.books.dock.placeholder.multiple")).asCentered()
        )
}