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
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.concurrent.TimeUnit

/**
 * Responsible for displaying a Google Book's thumbnail and providing the ability to open the [GoogleBookPreviewTabItem].
 *
 * It takes care of caching and loading of the images.
 */
class ThumbnailArea(private val context: Context) : VBox(10.0) {

    /**
     * The observable-value representing the actual thumbnail [Image]
     */
    private val thumbnail: ObjectProperty<Image?> = SimpleObjectProperty()

    /**
     * The observable-value representing the current volume that's thumbnail is displayed
     */
    private val volume: ObjectProperty<Volume> = object : SimpleObjectProperty<Volume>() {
        override fun invalidated() {
            retrieveThumbnail(get()) { thumbnail.set(it) }
        }
    }

    /**
     * The cache for the loaded thumbnails. The unused thumbnails will be expired after 1 minute.
     */
    private val thumbnailCache: Cache<Volume, Image> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    init {
        buildUI()
    }

    fun volumeProperty() = volume

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

    /**
     * Checks the cache and retrieves the thumbnail for the given volume
     *
     * @param volume the volume we want the thumbnail of
     * @param onAvailable the function called when the image is available
     */
    private fun retrieveThumbnail(volume: Volume?, onAvailable: (Image?) -> Unit) {
        volume?.let {
            thumbnailCache.getIfPresent(volume)?.let(onAvailable) ?: volume.volumeInfo?.imageLinks?.thumbnail?.let {
                val image = Image(it, true)
                thumbnailCache.put(volume, image)
                onAvailable(image)
            }
        } ?: onAvailable(null)
    }

    /**
     * The area where the image is displayed
     */
    private inner class Thumbnail : StackPane() {

        private var content: Node
            get() = children[0]
            set(value) {
                children.setAll(value)
            }

        /**
         * Gives back `true` if the mouse-event represents a double-click
         */
        private val MouseEvent.isDoubleClick get() = button == MouseButton.PRIMARY && clickCount == 2

        /**
         * The place-holder displayed when the image is not available
         */
        private val placeHolder = icon("image-icon").styleClass("thumbnail-place-holder")

        init {
            content = placeHolder
            thumbnail.addListener { _, _, newImage -> updateContent(newImage) }
        }

        /**
         * Places the appropriate content depending on the image is available or not (puts the [placeHolder] if needed)
         */
        private fun updateContent(image: Image?) {
            content = image?.wrapToImageView() ?: placeHolder
        }

        /**
         * Wraps the image into an [ImageView]
         */
        private fun Image.wrapToImageView() =
            ImageView(this).apply {
                cursor = Cursor.HAND
                setOnMouseClicked { event ->
                    when {
                        // Opening the Image in the browser for double-clicks
                        event.isDoubleClick -> SystemBrowser.browse(volume.get()?.volumeInfo?.imageLinks?.getLargest()!!)
                    }
                }
            }
    }
}