package com.dansoftware.libraryapp.gui.googlebooks.tile

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBookDetailsOverlay
import com.dansoftware.libraryapp.gui.util.SelectableLabel
import com.dansoftware.libraryapp.gui.window.undecorate.control.TitleBar
import com.dansoftware.libraryapp.locale.I18N
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class GoogleBookTile(private val context: Context, private val volume: Volume) :
    HBox(10.0, Thumbnail(volume), Info(volume)) {

    init {
        styleClass.add("google-book-tile")
        buildClickPolicy()
    }

    private fun buildClickPolicy() {
        setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY && it.clickCount == 2)
                context.showOverlay(GoogleBookDetailsOverlay(context, volume))
        }
    }

    private class Thumbnail(volume: Volume) : StackPane() {
        init {
            children.add(
                volume.volumeInfo
                    ?.imageLinks
                    ?.thumbnail
                    ?.let { ImageView(it) }
                    ?: PlaceHolder()
            )
        }

        private class PlaceHolder : Label(I18N.getGoogleBooksImportValue("google.books.table.thumbnail.not.available"))
    }

    private class Info(volume: Volume) : VBox(5.0) {

        init {
            setHgrow(this, Priority.ALWAYS)
            children.add(buildMarkLabel())
            children.add(Separator())
            children.add(buildVBox(volume))
        }

        private fun buildMarkLabel() =
            HBox(10.0, ImageView(Image("/com/dansoftware/libraryapp/image/util/google_12px.png")),
                Label(I18N.getGoogleBooksImportValue("google.books.tile.title")).also {
                    it.styleClass.add("tile-title")
                })

        private fun buildVBox(volume: Volume): VBox =
            VBox(3.0).also {
                setVgrow(it, Priority.ALWAYS)
                it.children.add(buildTitle(volume))
                it.children.add(buildSubtitle(volume))
                it.children.add(buildAuthorsLabel(volume))
            }

        private fun buildTitle(volume: Volume) =
            SelectableLabel(volume.volumeInfo?.title).also {
                it.styleClass.add("title-label")
            }

        private fun buildSubtitle(volume: Volume) =
            SelectableLabel(volume.volumeInfo?.subtitle).also {
                it.styleClass.add("sub-title-label")
            }

        private fun buildAuthorsLabel(volume: Volume) =
            SelectableLabel(volume.volumeInfo?.authors?.joinToString(", ")).also {
                it.styleClass.add("authors-label")
            }
    }
}