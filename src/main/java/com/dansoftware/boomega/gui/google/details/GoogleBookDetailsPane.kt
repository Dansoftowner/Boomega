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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.FixedFontMaterialDesignIconView
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.google.preview.GoogleBookPreviewTabItem
import com.dansoftware.boomega.gui.imgviewer.ImageViewerActivity
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.pnikosis.html2markdown.HTML2Md
import com.sandec.mdfx.MarkdownView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.geometry.NodeOrientation
import javafx.geometry.Side
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import java.util.concurrent.TimeUnit

class GoogleBookDetailsPane(private val context: Context) : HBox(15.0) {

    private val volume: ObjectProperty<Volume> = object : SimpleObjectProperty<Volume>() {
        override fun invalidated() {
            handleNewVolume(get())
        }
    }

    private val thumbnail: ObjectProperty<Image?> = SimpleObjectProperty()
    private val description: StringProperty = SimpleStringProperty()

    private val thumbnailCache: Cache<Volume, Image> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    private val descriptionCache: Cache<Volume, String> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add("google-book-details-pane")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        minHeight = 0.0
        buildUI()
    }

    constructor(context: Context, volume: Volume) : this(context) {
        this.volume.set(volume)
    }

    private fun handleNewVolume(volume: Volume?) {
        retrieveThumbnail(volume) { value -> thumbnail.set(value) }
        retrieveDescription(volume) { value -> description.set(value) }
    }

    private fun retrieveThumbnail(volume: Volume?, onAvailable: (Image?) -> Unit) {
        thumbnailCache.getIfPresent(volume)?.let { onAvailable(it) } ?: volume?.volumeInfo?.imageLinks?.thumbnail?.let {
            Image(it, true).also { image ->
                thumbnailCache.put(volume, image)
                onAvailable(image)
            }
        } ?: onAvailable(null)
    }

    private fun retrieveDescription(volume: Volume?, onAvailable: (String?) -> Unit) {
        descriptionCache.getIfPresent(volume)?.let { onAvailable(it) }
            ?: volume?.volumeInfo?.description.let { description ->
                CachedExecutor.submit(
                    object : Task<String>() {
                        override fun call(): String? = description?.let(HTML2Md::convert)
                    }.apply {
                        setOnSucceeded {
                            value?.let { descriptionCache.put(volume, it) }
                            onAvailable(value)
                        }
                    }
                )
            }
    }

    private fun buildUI() {
        children.add(ThumbnailArea())
        children.add(TabArea())
    }

    fun volumeProperty() = volume

    private inner class ThumbnailArea() : VBox(10.0) {
        init {
            buildUI()
        }

        private fun buildUI() {
            children.add(Thumbnail())
            children.add(buildPreviewButton())
        }

        private fun buildPreviewButton() = Button().apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.BOOK_OPEN_VARIANT)
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
                                            ImageViewerActivity(
                                                Image(volume.get()?.volumeInfo?.imageLinks?.getLargest()),
                                                context.contextWindow
                                            ).show()
                                    }
                                }
                            })
                        )
                    } ?: placeholder()
                }
            }

            private fun placeholder() {
                children.setAll(ThumbnailPlaceHolder())
            }
        }

        /**
         * Used as a place holder if the thumbnail is not available
         */
        private inner class ThumbnailPlaceHolder : FixedFontMaterialDesignIconView(MaterialDesignIcon.IMAGE, 50.0)
    }

    private inner class TabArea : TabPane() {
        init {
            styleClass.add(JMetroStyleClass.UNDERLINE_TAB_PANE)
            setHgrow(this, Priority.ALWAYS)
            initUI()
            initTabs()
            selectionModel.selectLast()
        }

        private fun initUI() {
            side = Side.BOTTOM
            tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
            initOrientation()
        }

        private fun initTabs() {
            tabs.add(Tab(i18n("google.books.details.sale"), SalePane()))
            tabs.add(Tab(i18n("google.books.table.column.desc"), DescriptionPane()))
            tabs.add(Tab(i18n("google.books.details.info"), VolumeInfoTable(volume)))
        }

        private fun initOrientation() {
            skinProperty().addListener(object : ChangeListener<Skin<*>?> {
                override fun changed(
                    observable: ObservableValue<out Skin<*>?>,
                    oldValue: Skin<*>?,
                    newValue: Skin<*>?
                ) {
                    newValue?.let {
                        lookup(".tab-header-area").nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
                        observable.removeListener(this)
                    }
                }

            })
        }
    }

    private inner class DescriptionPane : ScrollPane() {
        private val mdfxNode = object : MarkdownView() {
            init {
                mdStringProperty().bind(description)
                maxWidthProperty().bind(this@GoogleBookDetailsPane.prefWidthProperty())
            }

            override fun setLink(node: Node, link: String, description: String?) {
                node.cursor = Cursor.HAND
                node.setOnMouseClicked {
                    if (it.button == MouseButton.PRIMARY) {
                        SystemBrowser.browse(link)
                    }
                }

            }
        }

        init {
            isFitToWidth = true
            prefWidth = 500.0
            initPlaceHolderPolicy()
        }

        private fun initPlaceHolderPolicy() {
            placeHolder()
            volume.addListener { _, _, newVolume ->
                newVolume?.volumeInfo?.description?.let {
                    normalContent()
                } ?: placeHolder()
            }
        }

        private fun normalContent() {
            content = mdfxNode
            isFitToHeight = false
        }

        private fun placeHolder() {
            content = StackPane(Label(i18n("google.book.description.empty")))
            isFitToHeight = true
        }
    }

    private inner class SalePane : ScrollPane() {

        private var table: SaleInfoTable? = null
            get() = field ?: SaleInfoTable(volume).also {
                field = it
            }

        init {
            isFitToWidth = true
            isFitToHeight = true
            initPlaceHolderPolicy()
        }

        private fun initPlaceHolderPolicy() {
            content = buildNotSaleablePlaceHolder()
            volume.addListener { _, _, volume ->
                content = when (volume.saleInfo?.saleability) {
                    Volume.SaleInfo.FOR_SALE -> table!!
                    else -> buildNotSaleablePlaceHolder()
                }
            }
        }

        private fun buildNotSaleablePlaceHolder(): Node =
            StackPane(Label(i18n("google.books.details.notforsale")))
    }

}