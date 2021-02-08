package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.context.TitledOverlayBox
import com.dansoftware.libraryapp.gui.googlebooks.preview.GoogleBookPreviewActivity
import com.dansoftware.libraryapp.gui.imgviewer.ImageViewerActivity
import com.dansoftware.libraryapp.gui.util.*
import com.dansoftware.libraryapp.i18n.I18N
import com.dansoftware.libraryapp.util.ExploitativeExecutor
import com.pnikosis.html2markdown.HTML2Md
import com.sandec.mdfx.MDFXNode
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.concurrent.Task
import javafx.scene.Cursor
import javafx.scene.Group
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
import java.util.function.Consumer

/**
 * Used for displaying a [GoogleBookDetailsPane] as an overlay
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailsOverlay(context: Context, volume: Volume) :
    TitledOverlayBox(
        I18N.getValue("google.books.detail.title"),
        ImageView("/com/dansoftware/libraryapp/image/util/google_12px.png"),
        GoogleBookDetailsPane(context, volume)
    )

/**
 * Used for displaying more detailed data about a Google Book
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailsPane(private val context: Context, volume: Volume) : VBox(10.0) {

    init {
        styleClass.add("google-book-details-pane")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        buildUI(volume)
    }

    private fun buildUI(volume: Volume) {
        setVgrow(this, Priority.ALWAYS)
        children.add(HeaderArea(context, volume))
        ScrollArea().also {
            children.add(PaneChooserArea(context, it, volume))
            children.add(it)
        }
    }

    /**
     * The header area is the place where the thumbnail and the main information of the
     * book is displayed
     */
    private class HeaderArea(val context: Context, volume: Volume) : HBox(5.0) {

        init {
            styleClass.add("header-area")
            children.add(buildThumbnail(volume))
            children.add(MainHeaderArea(volume))
        }

        private fun buildThumbnail(volume: Volume): Node =
            volume.volumeInfo?.imageLinks?.thumbnail?.let {
                StackPane(ImageView(it).also { imageView ->
                    imageView.cursor = Cursor.HAND
                    imageView.setOnMouseClicked { event ->
                        when {
                            event.button == MouseButton.PRIMARY && event.clickCount == 2 ->
                                ImageViewerActivity(
                                    Image(volume.volumeInfo?.imageLinks?.getLargest()),
                                    context.contextWindow
                                ).show()
                        }
                    }
                })
            } ?: ThumbnailPlaceHolder()

        /**
         * The main header area is the place where the main information is displayed.
         * It is next to the thumbnail
         */
        private class MainHeaderArea(volume: Volume) : VBox(5.0) {
            init {
                HBox.setHgrow(this, Priority.ALWAYS)
                styleClass.add("main-header-area")
                volume.volumeInfo?.let { volumeInfo ->
                    this.children.add(buildTypeIndicator(volumeInfo))
                    this.children.add(Separator())
                    this.children.add(buildTitleLabel(volumeInfo))
                    this.children.add(buildSubtitleLabel(volumeInfo))
                    this.children.add(buildAuthorLabel(volumeInfo))
                    this.children.add(buildPublisherLabel(volumeInfo))
                    this.children.add(buildDateLabel(volumeInfo))
                } //?:
            }

            private fun buildTypeIndicator(volumeInfo: Volume.VolumeInfo): Node =
                HBox(5.0).also { hBox ->
                    when {
                        volumeInfo.isMagazine -> {
                            hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.NEWSPAPER).apply {
                                glyphSize = 25
                            })
                            hBox.children.add(Label(I18N.getValue("google.books.magazine")))
                        }
                        else -> {
                            hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.BOOK).apply { glyphSize = 25 })
                            hBox.children.add(Label(I18N.getValue("google.books.book")))
                        }
                    }
                }

            private fun buildTitleLabel(volumeInfo: Volume.VolumeInfo): Node =
                PropertyValuePair(
                    I18N.getValue("google.books.table.column.title"),
                    volumeInfo.title
                )

            private fun buildSubtitleLabel(volumeInfo: Volume.VolumeInfo): Node =
                PropertyValuePair(
                    I18N.getValue("google.books.table.column.subtitle"),
                    volumeInfo.subtitle
                )

            private fun buildAuthorLabel(volumeInfo: Volume.VolumeInfo): Node =
                PropertyValuePair(
                    I18N.getValue("google.books.table.column.author"),
                    volumeInfo.authors?.joinToString(", ")
                )

            private fun buildPublisherLabel(volumeInfo: Volume.VolumeInfo): Node =
                PropertyValuePair(
                    I18N.getValue("google.books.table.column.publisher"),
                    volumeInfo.publisher
                )

            private fun buildDateLabel(volumeInfo: Volume.VolumeInfo): Node =
                PropertyValuePair(
                    I18N.getValue("google.books.table.column.date"),
                    volumeInfo.publishedDate
                )
        }

        /**
         * Used as a place holder if the thumbnail is not available
         */
        private class ThumbnailPlaceHolder :
            StackPane(Label(I18N.getValue("google.books.table.thumbnail.not.available")))
    }

    /**
     * The pane chooser area is the place where the user can select the information panel or the
     * sale information panel to be shown
     */
    private class PaneChooserArea(val context: Context, scrollArea: ScrollPane, volume: Volume) :
        StackPane(Group(HBox(3.0).also { hBox ->
            val tgglGroup = ToggleGroup()
            tgglGroup.selectedToggleProperty().addListener { _, _, toggle ->
                @Suppress("UNCHECKED_CAST")
                (toggle.userData as Consumer<ScrollPane>).accept(scrollArea)
            }

            hBox.children.add(RadioToggleButton(I18N.getValue("google.books.details.info")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.userData = Consumer<ScrollPane> { it.content = InfoPane(context, volume) }
                toggle.isSelected = true
            })
            hBox.children.add(RadioToggleButton(I18N.getValue("google.books.details.sale")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.userData = Consumer<ScrollPane> { it.content = SaleInfoPane(volume) }
            })
        }))

    /**
     * The scroll-pane where the [InfoPane] or the [SaleInfoPane] is displayed
     */
    private class ScrollArea : ScrollPane() {
        init {
            //this.maxHeight = 250.0
            setVgrow(this, Priority.ALWAYS)
            this.isFitToWidth = true
            this.prefHeight = 250.0
        }
    }

    /**
     * The panel that shows the volume-info
     */
    private class InfoPane(val context: Context, volume: Volume) : VBox(10.0) {
        init {
            this.styleClass.add("info-panel")
            this.children.add(buildISBNLabel(volume))
            this.children.add(buildLangLabel(volume))
            this.children.add(buildDescriptionArea(volume))
            this.children.add(buildCategoriesIndicator(volume))
            this.children.add(buildRatingsIndicator(volume))
            this.children.add(buildPreviewHyperLink(volume))
        }

        private fun buildISBNLabel(volume: Volume): Node =
            HBox(5.0,
                PropertyNameLabel(I18N.getValue("google.books.table.column.isbn").plus(":")),
                VBox(2.0).also { vBox ->
                    volume.volumeInfo?.industryIdentifiers?.map { it.toString() }?.forEach {
                        vBox.children.add(
                            HBox(2.0,
                                Label("${8226.toChar()}"),
                                HighlightableLabel(it).apply {
                                    HBox.setHgrow(this, Priority.ALWAYS)
                                }
                            )
                        )
                    }
                }
            )

        private fun buildLangLabel(volume: Volume): Node =
            PropertyValuePair(
                I18N.getValue("google.books.table.column.lang"),
                Locale.forLanguageTag(volume.volumeInfo?.language).displayLanguage
            )

        private fun buildDescriptionArea(volume: Volume): Node =
            VBox(
                5.0,
                PropertyNameLabel(I18N.getValue("google.books.table.column.desc").plus(":"))
            ).also { hBox ->
                ExploitativeExecutor.submit(object : Task<String>() {
                    override fun call(): String? = volume.volumeInfo?.description?.let { HTML2Md.convert(it) }
                }.apply {
                    this.setOnSucceeded {
                        hBox.children.add(
                            this.value?.let { result ->
                                MDFXNode(result).apply {
                                    styleClass.add("description-area")
                                    prefWidth = 200.0
                                }
                            } ?: Label("-")
                        )
                    }
                })
            }

        private fun buildCategoriesIndicator(volume: Volume): Node =
            HBox(
                5.0,
                PropertyNameLabel(I18N.getValue("google.books.categories").plus(":")),
                volume.volumeInfo?.categories?.let { categories ->
                    VBox(2.0).also { listView ->
                        categories.forEach {
                            listView.children.add(
                                HBox(
                                    2.0,
                                    Label("${8226.toChar()}"),
                                    HighlightableLabel(it).apply { HBox.setHgrow(this, Priority.ALWAYS) }
                                )
                            )
                        }
                        HBox.setHgrow(listView, Priority.ALWAYS)
                    }
                } ?: Label("-")
            )

        private fun buildRatingsIndicator(volume: Volume): Node =
            HBox(
                5.0,
                PropertyNameLabel(I18N.getValue("google.books.table.column.rank").plus(":"))
            ).also {
                volume.volumeInfo?.averageRating?.let { rating ->
                    it.children.add(ReadOnlyRating(5, rating.toInt()))
                    volume.volumeInfo?.ratingsCount.let { ratingsCount ->
                        it.children.add(Label("(${ratingsCount})"))
                    }
                } ?: it.children.add(Label("-"))
            }

        private fun buildPreviewHyperLink(volume: Volume): Node =
            StackPane(WebsiteHyperLink(
                I18N.getValue("google.books.details.preview"),
                volume.volumeInfo?.previewLink
            ).apply {
                contextMenu = ContextMenu().also { menu ->
                    menu.items.add(
                        MenuItem(I18N.getValue("google.books.preview.open.embedded"))
                            .action { GoogleBookPreviewActivity(volume, context.contextWindow).show() }
                    )
                }
            })
    }

    /**
     * The panel that shows the sale-info
     */
    private class SaleInfoPane(volume: Volume) : VBox(10.0) {
        init {
            when (volume.saleInfo?.saleability) {
                Volume.SaleInfo.FOR_SALE -> {
                    this.children.add(buildEBookIndicator(volume))
                    this.children.add(buildCountryIndicator(volume))
                    this.children.add(buildListPriceLabel(volume))
                    this.children.add(buildRetailPriceLabel(volume))
                    this.children.add(buildBuyLinkLabel(volume))
                }
                else -> {
                    this.children.add(buildNotSaleablePlaceHolder())
                }
            }
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
            StackPane(
                Group(
                    HBox(
                        5.0,
                        MaterialDesignIconView(MaterialDesignIcon.GOOGLE_PLAY),
                        WebsiteHyperLink(
                            I18N.getValue("google.books.details.sale.buylink"),
                            volume.saleInfo?.buyLink
                        )
                    )
                )
            )

        private fun buildNotSaleablePlaceHolder(): Node =
            Label(I18N.getValue("google.books.details.notforsale")).apply {
                styleClass.add("not-for-sale-place-holder")
            }
    }

    //Utility classes

    private class PropertyValuePair(key: String, value: String?) : HBox(5.0) {
        init {
            children.add(PropertyNameLabel("$key:"))
            children.add(HighlightableLabel(value ?: "-").also {
                HBox.setHgrow(it, Priority.ALWAYS)
            })
        }
    }

    private class PropertyNameLabel(initial: String? = null) : Label(initial) {
        init {
            styleClass.add("property-mark-label")
        }
    }
}