package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.locale.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

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
                                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.NEWSPAPER).also { it.glyphSize = 25 })
                                hBox.children.add(Label(I18N.getGoogleBooksImportValue("google.books.magazine")))
                            }
                            else -> {
                                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.BOOK).also { it.glyphSize = 25 })
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


            //TODO: creating a normal thumbnail place holder
            private class ThumbnailPlaceHolder :
                StackPane(Label(I18N.getGoogleBooksImportValue("google.books.table.thumbnail.not.available")))
        }

        private class PaneChooserArea(scrollArea: ScrollPane, volume: Volume) : StackPane(Group(HBox(3.0).also { hBox ->
            val tgglGroup = ToggleGroup()

            hBox.children.add(ToggleButton(I18N.getGoogleBooksImportValue("google.books.details.info")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.selectedProperty().addListener { _, _, isSelected ->
                    scrollArea.content = if (isSelected) InfoPane(volume) else SaleInfoPane(volume)
                }
                toggle.isSelected = true
            })
            hBox.children.add(ToggleButton(I18N.getGoogleBooksImportValue("google.books.details.sale")).also { toggle ->
                toggle.toggleGroup = tgglGroup
            })
        }))

        private class ScrollArea : ScrollPane() {
            init {

            }
        }

        private class InfoPane(volume: Volume) : ScrollPane() {

        }

        private class SaleInfoPane(volume: Volume) : ScrollPane()

        private class PropertyValuePair(key: String, value: String?): HBox(5.0) {
            init {
                children.add(PropertyNameLabel("$key:"))
                children.add(Label(value ?: "-"))
            }
        }

        private class PropertyNameLabel(initial: String? = null): Label(initial) {
            init {
                styleClass.add("property-mark-label")
            }
        }
    }
}