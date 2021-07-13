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
import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.gui.control.ReadOnlyRating
import com.dansoftware.boomega.gui.control.WebsiteHyperLink
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.google.preview.GoogleBookPreviewTabItem
import com.dansoftware.boomega.gui.imgviewer.ImageViewerActivity
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.pnikosis.html2markdown.HTML2Md
import com.sandec.mdfx.MarkdownView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
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
import java.util.*
import java.util.concurrent.TimeUnit

class GoogleBookDetailsPane(private val context: Context) : HBox(15.0) {

    private val volume: ObjectProperty<Volume> = object : SimpleObjectProperty<Volume>() {
        override fun invalidated() {
            handleNewVolume(get())
        }
    }

    private val thumbnail: ObjectProperty<Image?> = SimpleObjectProperty()
    private val title: StringProperty = SimpleStringProperty()
    private val subtitle: StringProperty = SimpleStringProperty()
    private val authors: StringProperty = SimpleStringProperty()
    private val publisher: StringProperty = SimpleStringProperty()
    private val date: StringProperty = SimpleStringProperty()
    private val language: StringProperty = SimpleStringProperty()
    private val averageRating: DoubleProperty = SimpleDoubleProperty()
    private val previewLink: StringProperty = SimpleStringProperty()
    private val description: StringProperty = SimpleStringProperty()
    private val industryIdentifiers: ObjectProperty<List<String>?> = SimpleObjectProperty()
    private val categories: ObjectProperty<List<String>?> = SimpleObjectProperty()

    private val thumbnailCache: Cache<Volume, Image> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    private val descriptionCache: Cache<Volume, String> =
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build()

    init {
        styleClass.add("google-book-details-pane")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        minHeight = 0.0
        VBox.setVgrow(this, Priority.ALWAYS)
        buildUI()
    }

    constructor(context: Context, volume: Volume) : this(context) {
        this.volume.set(volume)
    }

    private fun handleNewVolume(volume: Volume?) {
        title.set(volume?.volumeInfo?.title)
        subtitle.set(volume?.volumeInfo?.subtitle)
        authors.set(volume?.volumeInfo?.authors?.joinToString(", "))
        publisher.set(volume?.volumeInfo?.publisher)
        date.set(volume?.volumeInfo?.publishedDate)
        language.set(volume?.volumeInfo?.language?.let(Locale::forLanguageTag)?.displayLanguage)
        previewLink.set(volume?.volumeInfo?.previewLink)
        averageRating.value = volume?.volumeInfo?.averageRating
        industryIdentifiers.set(volume?.volumeInfo?.industryIdentifiers?.map(Volume.VolumeInfo.IndustryIdentifier::toString))
        categories.set(volume?.volumeInfo?.categories)
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
            children.add(buildTypeIndicator())
            children.add(buildPreviewHyperlink())
        }

        private fun buildTypeIndicator(): Node =
            HBox(5.0).run {
                volume.addListener { _, _, newVolume ->
                    newVolume?.volumeInfo?.let {
                        when {
                            it.isMagazine -> {
                                children.setAll(
                                    FixedFontMaterialDesignIconView(MaterialDesignIcon.NEWSPAPER, 17.0),
                                    StackPane(Label(I18N.getValue("google.books.magazine")))
                                )
                            }
                            else -> {
                                children.setAll(
                                    FixedFontMaterialDesignIconView(MaterialDesignIcon.BOOK, 17.0),
                                    StackPane(Label(I18N.getValue("google.books.book")))
                                )
                            }
                        }
                    } ?: children.clear()
                }
                StackPane(this)
            }

        private fun buildPreviewHyperlink() = Hyperlink(I18N.getValue("google.books.details.preview")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.APPLICATION)
            contextMenu = ContextMenu(
                MenuItem(I18N.getValue("google.book.preview.browser"))
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
            tabs.add(Tab(I18N.getValue("google.books.details.sale"), SalePane()))
            tabs.add(Tab(I18N.getValue("google.books.table.column.desc"), DescriptionPane()))
            tabs.add(Tab(I18N.getValue("google.books.details.info"), InfoPane()))
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

    private inner class InfoPane : ScrollPane() {
        init {
            isFitToWidth = true
            styleClass.add("info-panel")
            buildUI()
        }

        private fun buildUI() {
            content = buildVBox()
        }

        private fun buildVBox() = VBox(10.0).apply {
            //children.add(buildTypeIndicator())
            children.add(buildTitleLabel())
            children.add(buildSubtitleLabel())
            children.add(buildAuthorLabel())
            children.add(buildPublisherLabel())
            children.add(buildDateLabel())
            children.add(buildLangLabel())
            children.add(buildISBNIndicator())
            children.add(buildCategoriesIndicator())
            children.add(buildRatingsIndicator())
        }

        private fun buildTypeIndicator(): Node =
            HBox(5.0).also { hBox ->
                volume.addListener { _, _, newVolume ->
                    newVolume?.volumeInfo?.let {
                        when {
                            it.isMagazine -> {
                                hBox.children.setAll(
                                    FixedFontMaterialDesignIconView(MaterialDesignIcon.NEWSPAPER, 20.0),
                                    Label(I18N.getValue("google.books.magazine"))
                                )
                            }
                            else -> {
                                hBox.children.setAll(
                                    FixedFontMaterialDesignIconView(MaterialDesignIcon.BOOK, 20.0),
                                    Label(I18N.getValue("google.books.book"))
                                )
                            }
                        }
                    } ?: hBox.children.clear()

                }
            }

        private fun buildTitleLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.title"),
                title
            )

        private fun buildSubtitleLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.subtitle"),
                subtitle
            )

        private fun buildAuthorLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.author"),
                authors
            )

        private fun buildPublisherLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.publisher"),
                publisher
            )

        private fun buildDateLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.date"),
                date
            )

        private fun buildLangLabel() =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.lang"),
                language
            )

        private fun buildISBNIndicator(): Node =
            VBox(5.0,
                PropertyNameLabel(I18N.getValue("google.books.table.column.isbn").plus(":")),
                VBox(2.0).also { vBox ->
                    industryIdentifiers.addListener { _, _, identifiers ->
                        vBox.children.setAll(identifiers?.map {
                            HBox(2.0,
                                Label("${8226.toChar()}"),
                                HighlightableLabel(it).apply {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }
                            )
                        } ?: listOf(Label(" - ")))
                    }
                }
            )


        private fun buildCategoriesIndicator() =
            VBox(5.0,
                PropertyNameLabel(I18N.getValue("google.books.categories").plus(":")),
                VBox(2.0).also { vBox ->
                    categories.addListener { _, _, categories ->
                        vBox.children.setAll(categories?.map {
                            HBox(2.0,
                                Label("${8226.toChar()}"),
                                HighlightableLabel(it).apply {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }
                            )
                        } ?: listOf(Label(" - ")))
                    }
                }
            )

        private fun buildRatingsIndicator() =
            VBox(
                5.0,
                PropertyNameLabel(I18N.getValue("google.books.table.column.rank").plus(":")),
                ReadOnlyRating(5, 0).apply {
                    ratingProperty().bind(averageRating)
                }
            )

        private fun buildBrowserButton() = Button().apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.GOOGLE_CHROME)
            text = I18N.getValue("google.book.preview.browser")
            setOnAction {
                volume.get()?.volumeInfo?.previewLink?.let {
                    SystemBrowser.browse(it)
                }
            }
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
            content = StackPane(Label(I18N.getValue("google.book.description.empty")))
            isFitToHeight = true
        }
    }

    private inner class SalePane : ScrollPane() {
        init {
            isFitToWidth = true
            isFitToHeight = true
            initPlaceHolderPolicy()
        }

        private fun initPlaceHolderPolicy() {
            placeholder()
            volume.addListener { _, _, volume ->
                when (volume.saleInfo?.saleability) {
                    Volume.SaleInfo.FOR_SALE -> normalContent(volume)
                    else -> placeholder()
                }
            }
        }

        private fun placeholder() {
            content = buildNotSaleablePlaceHolder()
        }

        private fun normalContent(volume: Volume) {
            content = VBox(
                10.0,
                buildEBookIndicator(volume),
                buildCountryIndicator(volume),
                buildListPriceLabel(volume),
                buildRetailPriceLabel(volume),
                buildBuyLinkLabel(volume)
            )
        }

        private fun buildEBookIndicator(volume: Volume): Node =
            PropertyValuePair(
                I18N.getValue("google.books.details.sale.isebook"),
                I18N.getValues().getString(
                    when (volume.saleInfo?.isEbook) {
                        true -> "Dialog.yes.button"
                        else -> "Dialog.no.button"
                    }
                )
            )

        private fun buildCountryIndicator(volume: Volume): Node =
            PropertyValuePair(
                I18N.getValue("google.books.details.sale.country"),
                volume.saleInfo?.country
            )

        private fun buildListPriceLabel(volume: Volume): Node =
            PropertyValuePair(
                I18N.getValue("google.books.details.sale.listprice"),
                volume.saleInfo?.listPrice.toString()
            )

        private fun buildRetailPriceLabel(volume: Volume): Node =
            PropertyValuePair(
                I18N.getValue("google.books.details.sale.retailprice"),
                volume.saleInfo?.retailPrice.toString()
            )

        private fun buildBuyLinkLabel(volume: Volume): Node =
            HBox(
                5.0,
                MaterialDesignIconView(MaterialDesignIcon.GOOGLE_PLAY),
                WebsiteHyperLink(
                    I18N.getValue("google.books.details.sale.buylink"),
                    volume.saleInfo?.buyLink
                )
            )


        private fun buildNotSaleablePlaceHolder(): Node =
            StackPane(Label(I18N.getValue("google.books.details.notforsale")))
    }

    //Utility classes

    private class PropertyValuePair(key: String, value: StringProperty) : VBox(5.0) {
        init {
            children.add(PropertyNameLabel("$key:"))
            children.add(HighlightableLabel().apply {
                textProperty().bind(value)
            })
            visibleProperty().bind(value.isNotNull)
            managedProperty().bind(value.isNotNull)
        }

        constructor(key: String, value: String?) : this(key, SimpleStringProperty(value))
    }

    private class PropertyNameLabel(initial: String? = null) : Label(initial) {
        init {
            styleClass.add("property-mark-label")
        }
    }
}