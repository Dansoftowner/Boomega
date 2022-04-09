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

import com.dansoftware.boomega.update.Release
import javafx.geometry.Insets
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DetailsSegmentView(private val release: Release) : VBox() {

    init {
        children.add(buildPreviewScrollPane())
    }

    private fun buildPreviewScrollPane() = ScrollPane().apply {
        setMargin(this, Insets(0.0, 0.0, 10.0, 0.0))
        prefHeight = 200.0
        prefWidth = 200.0
        isFitToWidth = true
        isFitToHeight = true
        content = PreviewMarkdownView(release.description)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DetailsSegmentView::class.java)
    }
}