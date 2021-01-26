package com.dansoftware.libraryapp.gui.googlebooks.tile

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBookDetailsOverlay
import com.dansoftware.libraryapp.gui.util.HighlightableLabel
import com.dansoftware.libraryapp.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

/**
 * A [GoogleBookTile] is a gui-tile that shows some information about a [Volume].
 *
 * @author Daniel Gyorffy
 */
class GoogleBookTile(
    private val context: Context,
    private val volume: Volume
) : HBox(10.0, Thumbnail(volume), Info(volume)) {

    init {
        styleClass.add("google-book-tile")
        buildClickPolicy()
    }

    constructor(context: Context, volume: Volume, onClosed: Consumer<Volume>) : this(context, volume) {
        children.add(CloseButton { onClosed.accept(volume) })
    }

    private fun buildClickPolicy() {
        setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY && it.clickCount == 2)
                context.showOverlay(GoogleBookDetailsOverlay(context, volume))
        }
    }

    private class CloseButton(onAction: EventHandler<ActionEvent>) : Button() {
        init {
            this.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            this.graphic = MaterialDesignIconView(MaterialDesignIcon.CLOSE)
            this.onAction = onAction
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
            VBox(3.0).also { vBox ->
                setVgrow(vBox, Priority.ALWAYS)
                vBox.children.also { elements ->
                    StringUtils.getIfBlank(volume.volumeInfo?.title) { null }?.let { elements.add(buildTitle(it)) }
                    StringUtils.getIfBlank(volume.volumeInfo?.subtitle) { null }
                        ?.let { elements.add(buildSubtitle(it)) }
                    volume.volumeInfo?.authors?.let { elements.add(buildAuthorsLabel(it)) }
                }
            }

        private fun buildTitle(title: String) =
            HighlightableLabel(title).also { it.styleClass.add("title-label") }

        private fun buildSubtitle(subtitle: String) =
            HighlightableLabel(subtitle).also { it.styleClass.add("sub-title-label") }

        private fun buildAuthorsLabel(authors: List<String>) =
            HighlightableLabel(authors.joinToString(", ")).also { it.styleClass.add("authors-label") }
    }
}