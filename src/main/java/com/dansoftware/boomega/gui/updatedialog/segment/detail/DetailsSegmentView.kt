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

package com.dansoftware.boomega.gui.updatedialog.segment.detail

import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.update.UpdateInformation
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.sandec.mdfx.MarkdownView
import javafx.geometry.Insets
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DetailsSegmentView(private val context: Context, private val updateInformation: UpdateInformation) : VBox() {

    private val previewScrollPane: ScrollPane

    init {
        children.add(buildPreviewScrollPane().also { previewScrollPane = it })
        load()
    }

    private fun buildPreviewScrollPane() = ScrollPane().apply {
        prefHeight = 200.0
        prefWidth = 200.0
        isFitToWidth = true
        setMargin(this, Insets(0.0, 0.0, 10.0, 0.0))
    }

    private fun load() {
        CachedExecutor.submit(buildLoadTask())
    }

    private fun buildLoadTask() = PreviewTextLoadTask(updateInformation).apply {

        setOnRunning {
            ProgressBar().apply {
                progress = ProgressIndicator.INDETERMINATE_PROGRESS
                previewScrollPane.content = this
            }
        }

        setOnFailed {
            val cause = it.source.exception
            logger.error("Couldn't load the markdown-preview", cause)
            previewScrollPane.content = PreviewErrorPlaceHolder(context, cause)
        }

        //if the task succeeded, we render it as a markdown-text into a javaFX node
        setOnSucceeded {
            previewScrollPane.apply {
                content = MarkdownView(value)
                isFitToHeight = false
                isFitToWidth = true
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DetailsSegmentView::class.java)
    }
}