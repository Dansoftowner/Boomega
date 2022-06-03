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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.google.preview.GoogleBookPreviewTabItem
import com.dansoftware.boomega.gui.imgviewer.ImageViewerWindow
import com.dansoftware.boomega.gui.util.SystemBrowser
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.styleClass
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.rest.google.books.Volume
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.concurrent.TimeUnit

class ThumbnailArea(private val context: Context) : VBox(10.0) {

    private val thumbnail: ObjectProperty<Image?> = SimpleObjectProperty()
    private val volume: ObjectProperty<Volume> = object : SimpleObjectProperty<Volume>() {
        override fun invalidated() {
            retrieveThumbnail(get()) { thumbnail.set(it) }
        }
    }

    private val thumbnailCache: Cache<Volume, Image> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(Thumbnail())
        children.add(buildPreviewButton())
    }

    private fun buildPreviewButton() = Button().apply {
        graphic = icon("book-preview-icon")
        text = i18n("google.books.details.preview")
        maxWidth = Double.MAX_VALUE
        contextMenu = ContextMenu(
            MenuItem(i18n("google.book.preview.browser"))
                .action { volume.get()?.volumeInfo?.previewLink?.let(SystemBrowser::browse) }
        )
        setOnAction {
            context.sendRequest(DatabaseView.TabItemShowRequest(GoogleBookPreviewTabItem(context, volume.get())))
        }
    }

    private fun retrieveThumbnail(volume: Volume?, onAvailable: (Image?) -> Unit) {
        thumbnailCache.getIfPresent(volume)?.let(onAvailable) ?: volume?.volumeInfo?.imageLinks?.thumbnail?.let {
            Image(it, true).also { image ->
                thumbnailCache.put(volume, image)
                onAvailable(image)
            }
        } ?: onAvailable(null)
    }

    private inner class Thumbnail() : StackPane() {
        init {
            placeholder()
            thumbnail.addListener { _, _, newImage ->
                newImage?.let {
                    children.setAll(
                        StackPane(ImageView(it).apply {
                            cursor = Cursor.HAND
                            setOnMouseClicked { event ->
                                when {
                                    event.button == MouseButton.PRIMARY && event.clickCount == 2 ->
                                        ImageViewerWindow(
                                            content = Image(volume.get()?.volumeInfo?.imageLinks?.getLargest()),
                                            owner = context.contextWindow
                                        ).show()
                                }
                            }
                        })
                    )
                } ?: placeholder()
            }
        }

        private fun placeholder() {
            children.setAll(icon("image-icon").styleClass("thumbnail-place-holder"))
        }
    }
}