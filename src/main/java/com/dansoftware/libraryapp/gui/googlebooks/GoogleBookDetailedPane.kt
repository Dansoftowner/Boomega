package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.locale.I18N
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class GoogleBookDetailedPane(volume: Volume) : StackPane(Group(ActualPanel(volume))) {

    init {
        isPickOnBounds = false
    }

    private class ActualPanel(volume: Volume) : VBox(10.0) {

        init {
            children.add(HeaderArea(volume))
            children.add(PaneChooserArea(this, volume))
        }


        private class HeaderArea(volume: Volume) : HBox(5.0) {
            init {
                children.add(buildThumbnail(volume))
                children.add(MainHeaderArea(volume))
            }

            private fun buildThumbnail(volume: Volume): Node =
                volume.volumeInfo?.imageLinks?.thumbnail?.let { ImageView(it) } ?: ThumbnailPlaceHolder()


            private class MainHeaderArea(val volume: Volume) : VBox() {
            }


            //TODO: creating a normal thumbnail place holder
            private class ThumbnailPlaceHolder() : Node() {

            }
        }

        private class PaneChooserArea(parent: ActualPanel, volume: Volume) : StackPane(Group(HBox(3.0).also { hBox ->
            val tgglGroup = ToggleGroup()
            hBox.children.add(ToggleButton(I18N.getGoogleBooksImportValue("google.books.details.info")).also { toggle ->
                toggle.toggleGroup = tgglGroup
                toggle.selectedProperty().addListener { _, _, isSelected ->
                    when {
                        isSelected -> {
                            parent.children.removeIf { it is SaleInfoPane }
                            parent.children.add(InfoPane(volume))
                        }
                        else -> {
                            parent.children.removeIf { it is InfoPane }
                            parent.children.add(SaleInfoPane(volume))
                        }
                    }
                }
                toggle.isSelected = true
            })
            hBox.children.add(ToggleButton(I18N.getGoogleBooksImportValue("google.books.details.sale")).also { toggle ->
                toggle.toggleGroup = tgglGroup
            })
        }))

        private class InfoPane(volume: Volume) : ScrollPane() {

        }

        private class SaleInfoPane(volume: Volume) : ScrollPane()
    }
}