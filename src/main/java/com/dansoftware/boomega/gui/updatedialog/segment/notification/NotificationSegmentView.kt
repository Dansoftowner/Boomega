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

package com.dansoftware.boomega.gui.updatedialog.segment.notification

import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.update.Release
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font

class NotificationSegmentView(
    private val release: Release
) : VBox(10.0) {

    init {
        padding = Insets(10.0)
        buildUI()
    }

    private fun buildUI() {
        children.add(Header())
        children.add(VersionArea(release))
    }

    private class Header : StackPane() {

        init {
            setVgrow(this, Priority.SOMETIMES)
            buildUI()
        }

        private fun buildUI() {
            children.add(buildLabel())
        }

        private fun buildLabel() = Label(I18N.getValue("update.available")).apply {
            styleClass.add("update-view-available-label")
            padding = Insets(10.0)
            font = Font.font(32.0)
        }
    }

    private class VersionArea(private val release: Release) : StackPane() {
        init {
            buildUI()
        }

        private fun buildUI() {
            children.add(buildGroup())
        }

        private fun buildGroup() = Group(buildHBox())

        private fun buildHBox() = HBox(10.0).apply {
            children.add(buildDescriptionLabel("update.dialog.current_version"))
            children.add(buildCurrentVersionLabel())
            children.add(Separator(Orientation.VERTICAL))
            children.add(buildDescriptionLabel("update.dialog.next_version"))
            children.add(buildNextVersionLabel())
        }

        private fun buildDescriptionLabel(i18N: String) = Label(I18N.getValue(i18N)).apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        private fun buildCurrentVersionLabel() = Label(System.getProperty("boomega.version")).apply {
            HBox.setHgrow(this, Priority.SOMETIMES)
        }

        private fun buildNextVersionLabel() = Label(release.version).apply {
            HBox.setHgrow(this, Priority.SOMETIMES)
        }
    }


}