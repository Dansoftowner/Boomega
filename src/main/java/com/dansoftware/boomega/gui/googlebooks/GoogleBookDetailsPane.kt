package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.googlebooks.Volume
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.TitledOverlayBox
import com.dansoftware.boomega.gui.control.*
import com.dansoftware.boomega.gui.googlebooks.preview.GoogleBookPreviewActivity
import com.dansoftware.boomega.gui.imgviewer.ImageViewerActivity
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.pnikosis.html2markdown.HTML2Md
import com.sandec.mdfx.MDFXNode
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.geometry.NodeOrientation
import javafx.geometry.Side
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
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Used for displaying a [GoogleBookDetailsPane] as an overlay
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailsOverlay(context: Context, volume: Volume) :
    TitledOverlayBox(
        I18N.getValue("google.books.detail.title"),
        ImageView("/com/dansoftware/boomega/image/util/google_12px.png"),
        GoogleBookDetailsPane(context, volume)
    )

class GoogleBookDetailsPane(private val context: Context) : HBox(15.0) {

    private val volume: ObjectProperty<Volume> = object : SimpleObjectProperty<Volume>() {
        override fun invalidated() {
            get().let {
                title.set(it?.volumeInfo?.title)
                subtitle.set(it?.volumeInfo?.subtitle)
                authors.set(it?.volumeInfo?.authors?.joinToString(", "))
                publisher.set(it?.volumeInfo?.publisher)
                date.set(it?.volumeInfo?.publishedDate)
                language.set(it?.volumeInfo?.language?.let(Locale::forLanguageTag)?.displayLanguage)
                previewLink.set(it?.volumeInfo?.previewLink)
                averageRating.value = it?.volumeInfo?.averageRating
                industryIdentifiers.set(it?.volumeInfo?.industryIdentifiers?.map(Volume.VolumeInfo.IndustryIdentifier::toString))
                categories.set(it?.volumeInfo?.categories)
                retrieveThumbnail(it) { value -> thumbnail.set(value) }
                retrieveDescription(it) { value -> description.set(value) }
            }
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
        VBox.setVgrow(this, Priority.ALWAYS)
        buildUI()
    }

    constructor(context: Context, volume: Volume) : this(context) {
        this.volume.set(volume)
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
                GoogleBookPreviewActivity(volume.get()).show()
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

        private fun wrapToScrollPane(element: Node) = ScrollPane().apply {
            content = element
            isFitToWidth = true
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
        private val mdfxNode = object : MDFXNode() {
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

/**
 * Used for displaying more detailed data about a Google Book
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailsPaneOld(private val context: Context, volume: Volume) : VBox(10.0) {

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
                CachedExecutor.submit(object : Task<String>() {
                    override fun call(): String? = volume.volumeInfo?.description?.let(HTML2Md::convert)
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
            StackPane(
                WebsiteHyperLink(
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