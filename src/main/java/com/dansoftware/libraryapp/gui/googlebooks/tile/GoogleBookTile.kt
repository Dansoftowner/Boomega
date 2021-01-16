package com.dansoftware.libraryapp.gui.googlebooks.tile

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBookDetailsOverlay
import com.dansoftware.libraryapp.locale.I18N
import com.fasterxml.jackson.databind.type.PlaceholderForType
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class GoogleBookTile(private val context: Context, private val volume: Volume) :
    HBox(5.0, Thumbnail(volume), Info(volume)) {

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

    private class Info(volume: Volume) : StackPane() {

        init {
            children.add(Group(buildVBox(volume)))
        }

        private fun buildVBox(volume: Volume): VBox =
            VBox(3.0).also {

            }
    }
}