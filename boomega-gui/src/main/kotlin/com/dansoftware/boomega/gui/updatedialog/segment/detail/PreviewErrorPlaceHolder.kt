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
package com.dansoftware.boomega.gui.updatedialog.segment.detail

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.api.I18N
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

/**
 * A PreviewErrorPlaceHolder is a GUI object that is used as a placeholder for
 * the preview-area.
 */
class PreviewErrorPlaceHolder(private val context: Context, private val cause: Throwable) : StackPane() {
    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(buildRoot())
    }

    private fun buildRoot() = Group(buildVBox())

    private fun buildVBox() = VBox(5.0, buildLabel(), buildDetailButton())

    private fun buildLabel() = Label(I18N.getValue("update.details.preview.failed"))

    private fun buildDetailButton() = Button(I18N.getValue("update.details.preview.failed.more")).run {
        setOnAction {
            context.showErrorDialog(
                I18N.getValue("update.details.preview.failed"),
                null,
                cause as Exception
            ) { }
        }
        StackPane(this)
    }
}