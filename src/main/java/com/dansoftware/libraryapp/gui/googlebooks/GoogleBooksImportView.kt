package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.rcadd.RecordAddForm
import com.dansoftware.libraryapp.gui.rcadd.RecordAddModule
import com.dansoftware.libraryapp.locale.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import org.apache.commons.lang3.StringUtils

/**
 * A [GoogleBooksImportView] is a [GoogleBooksSearchView] that has an additional button
 * on the bottom that calls the [RecordAddModule] for importing the selected volume.
 *
 * @author Daniel Gyorffy
 */
private class GoogleBooksImportView(val context: Context) : GoogleBooksSearchView(context) {

    init {
        styleClass.add("google-books-import-view")
        table.selectionModel
        children.add(buildButton())
    }

    private fun buildButton(): Button =
        Button().also { btn ->
            btn.styleClass.add("google-books-import-btn")
            btn.prefWidthProperty().bind(this.widthProperty())
            btn.disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
            btn.textProperty().bind(
                SimpleStringProperty(I18N.getGoogleBooksImportValue("google.books.import.button"))
                    .concat(SimpleStringProperty(StringUtils.EMPTY).also { titleProp ->
                        table.selectionModel.selectedItemProperty().addListener { _, _, newItem ->
                            when {
                                newItem === null -> titleProp.set(StringUtils.EMPTY)
                                else -> titleProp.set(" (${newItem.volumeInfo?.title}) ")
                            }
                        }
                    })
            )
            btn.isDefaultButton = true
            btn.onAction = buildButtonAction()
        }

    private fun buildButtonAction() = EventHandler<ActionEvent> {
        context.showModule(RecordAddModule::class.java, RecordAddForm.Values().also { values ->
            table.selectionModel.selectedItem.also { volume ->
                values.authors(volume.volumeInfo?.authors?.joinToString(", "))
                values.date(volume.volumeInfo?.getPublishedDateObject())
                values.isbn(volume.volumeInfo?.industryIdentifiers?.find { it.isIsbn13 }?.identifier)
                values.language(volume.volumeInfo?.language)
                values.title(volume.volumeInfo?.title)
                values.subtitle(volume.volumeInfo?.subtitle)
                values.notes(volume.volumeInfo?.description)
                values.publisher(volume.volumeInfo?.publisher)
                values.googleVolumeObject(volume)
                values.rating(volume.volumeInfo?.averageRating?.toInt() ?: 5)
            }
        })
    }
}