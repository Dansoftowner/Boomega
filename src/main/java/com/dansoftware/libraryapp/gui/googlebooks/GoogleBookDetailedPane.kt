package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.util.RadioToggleButton
import com.dansoftware.libraryapp.gui.util.ReadOnlyRating
import com.dansoftware.libraryapp.gui.util.SelectableLabel
import com.dansoftware.libraryapp.locale.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass
import java.util.*
import java.util.function.Consumer

/**
 * Used for displaying more detailed data about a Google Book
 *
 * @author Daniel Gyorffy
 */
class GoogleBookDetailedPane(volume: Volume) : StackPane(Group(VBox(TitleBar(), MainVBox(volume)))) {

    init {
        isPickOnBounds = false
    }

    private class TitleBar : HBox(5.0) {
        init {
            styleClass.add("google-book-details-title-bar")
            children.add(ImageView("/com/dansoftware/libraryapp/image/util/google_12px.png"))
            children.add(Label(I18N.getGoogleBooksImportValue("google.books.detail.title")))
        }
    }

    private class MainVBox(volume: Volume) : VBox(10.0) {

        init {
            styleClass.add("google-book-details-pane")
            styleClass.add(JMetroStyleClass.BACKGROUND)
            buildUI(volume)
        }

        private fun buildUI(volume: Volume) {
            //TODO: Title bar
            children.add(HeaderArea(volume))
            val scrollArea = ScrollArea()
            children.add(PaneChooserArea(scrollArea, volume))
            children.add(scrollArea)
        }

        private class HeaderArea(volume: Volume) : HBox(5.0) {
            init {
                styleClass.add("header-area")
                children.add(buildThumbnail(volume))
                children.add(MainHeaderArea(volume))
            }

            private fun buildThumbnail(volume: Volume): Node =
                volume.volumeInfo?.imageLinks?.thumbnail?.let { StackPane(ImageView(it)) } ?: ThumbnailPlaceHolder()


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
                                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.NEWSPAPER).also {
                                    it.glyphSize = 25
                                })
                                hBox.children.add(Label(I18N.getGoogleBooksImportValue("google.books.magazine")))
                            }
                            else -> {
                                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.BOOK).also {
                                    it.glyphSize = 25
                                })
                                hBox.children.add(Label(I18N.getGoogleBooksImportValue("google.books.book")))
                            }
                        }
                    }

                private fun buildTitleLabel(volumeInfo: Volume.VolumeInfo): Node =
                    PropertyValuePair(
                        I18N.getGoogleBooksImportValue("google.books.table.column.title"),
                        volumeInfo.title
                    )

                private fun buildSubtitleLabel(volumeInfo: Volume.VolumeInfo): Node =
                    PropertyValuePair(
                        I18N.getGoogleBooksImportValue("google.books.table.column.subtitle"),
                        volumeInfo.subtitle
                    )

                private fun buildAuthorLabel(volumeInfo: Volume.VolumeInfo): Node =
                    PropertyValuePair(
                        I18N.getGoogleBooksImportValue("google.books.table.column.author"),
                        volumeInfo.authors?.joinToString(", ")
                    )

                private fun buildPublisherLabel(volumeInfo: Volume.VolumeInfo): Node =
                    PropertyValuePair(
                        I18N.getGoogleBooksImportValue("google.books.table.column.publisher"),
                        volumeInfo.publisher
                    )

                private fun buildDateLabel(volumeInfo: Volume.VolumeInfo): Node =
                    PropertyValuePair(
                        I18N.getGoogleBooksImportValue("google.books.table.column.date"),
                        volumeInfo.publishedDate
                    )
            }

            private class ThumbnailPlaceHolder :
                StackPane(Label(I18N.getGoogleBooksImportValue("google.books.table.thumbnail.not.available")))
        }

        private class PaneChooserArea(scrollArea: ScrollPane, volume: Volume) : StackPane(Group(HBox(3.0).also { hBox ->
            val tgglGroup = ToggleGroup()
            tgglGroup.selectedToggleProperty().addListener { _, _, toggle ->
                @Suppress("UNCHECKED_CAST")
                (toggle.userData as Consumer<ScrollPane>).accept(scrollArea)
            }

            hBox.children.add(RadioToggleButton(I18N.getGoogleBooksImportValue("google.books.details.info")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.userData = Consumer<ScrollPane> { it.content = InfoPane(volume) }
                toggle.isSelected = true
            })
            hBox.children.add(RadioToggleButton(I18N.getGoogleBooksImportValue("google.books.details.sale")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.userData = Consumer<ScrollPane> { it.content = SaleInfoPane(volume) }
            })
        }))

        private class ScrollArea : ScrollPane() {
            init {
                this.maxHeight = 250.0
                this.isFitToWidth = true
            }
        }

        private class InfoPane(volume: Volume) : VBox(10.0) {
            init {
                this.styleClass.add("info-panel")
                this.children.add(buildISBNLabel(volume))
                this.children.add(buildLangLabel(volume))
                this.children.add(buildDescriptionArea(volume))
                this.children.add(buildCategoriesIndicator(volume))
                this.children.add(buildRatingsIndicator(volume))
            }

            private fun buildISBNLabel(volume: Volume): Node =
                HBox(5.0,
                    PropertyNameLabel(I18N.getGoogleBooksImportValue("google.books.table.column.isbn").plus(":")),
                    VBox(2.0).also { vBox ->
                        volume.volumeInfo?.industryIdentifiers?.map { it.toString() }?.forEach {
                            vBox.children.add(SelectableLabel(it))
                        }
                    }
                )

            private fun buildLangLabel(volume: Volume): Node =
                PropertyValuePair(
                    I18N.getGoogleBooksImportValue("google.books.table.column.lang"),
                    Locale.forLanguageTag(volume.volumeInfo?.language).displayLanguage
                )

            private fun buildDescriptionArea(volume: Volume): Node =
                HBox(5.0,
                    PropertyNameLabel(I18N.getGoogleBooksImportValue("google.books.table.column.desc").plus(":")),
                    TextArea(volume.volumeInfo?.description).also {
                        it.isWrapText = true
                        it.prefWidth = Region.USE_COMPUTED_SIZE
                        it.prefHeight = Region.USE_COMPUTED_SIZE
                        it.isEditable = false
                    }
                )

            private fun buildCategoriesIndicator(volume: Volume): Node =
                HBox(
                    5.0,
                    PropertyNameLabel(I18N.getGoogleBooksImportValue("google.books.categories").plus(":")),
                    volume.volumeInfo?.categories?.let { categories ->
                        VBox(2.0).also { listView ->
                            categories.forEach {
                                listView.children.add(
                                    HBox(2.0,
                                        Label("${8226.toChar()}"),
                                        SelectableLabel(it)
                                    )
                                )
                            }
                        }
                    } ?: Label("-")
                )

            private fun buildRatingsIndicator(volume: Volume): Node =
                HBox(
                    5.0,
                    PropertyNameLabel(I18N.getGoogleBooksImportValue("google.books.table.column.rank").plus(":"))
                ).also {
                    volume.volumeInfo?.averageRating?.let { rating ->
                        it.children.add(ReadOnlyRating(5, rating.toInt()))
                        volume.volumeInfo?.ratingsCount.let { ratingsCount ->
                            it.children.add(Label("(${ratingsCount})"))
                        }
                    } ?: it.children.add(Label("-"))
                }
        }

        private class SaleInfoPane(volume: Volume) : VBox() {
            init {
            }
        }

    }

    //Utility classes

    private class PropertyValuePair(key: String, value: String?) : HBox(5.0) {
        init {
            children.add(PropertyNameLabel("$key:"))
            children.add(SelectableLabel(value ?: "-").also {
                HBox.setHgrow(it, Priority.ALWAYS)
            })
        }
    }

    private class PropertyNameLabel(initial: String? = null) : Label(initial) {
        init {
            styleClass.add("property-mark-label")
            this.isWrapText = true
        }
    }
}