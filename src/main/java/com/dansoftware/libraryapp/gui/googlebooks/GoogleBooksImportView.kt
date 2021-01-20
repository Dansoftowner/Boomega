package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.rcadd.RecordAddForm
import com.dansoftware.libraryapp.gui.rcadd.RecordAddModule
import com.dansoftware.libraryapp.locale.I18N
import javafx.scene.control.Button

private class GoogleBooksImportView(val context: Context) : GoogleBooksSearchView(context) {

    init {
        table.selectionModel
        children.add(buildButton())
    }

    private fun buildButton(): Button =
        Button().also { btn ->
            btn.prefWidthProperty().bind(this.widthProperty())
            btn.disableProperty().bind(table.selectionModel.selectedItemProperty().isNull)
            btn.text = I18N.getGoogleBooksImportValue("google.books.import.button")
            btn.isDefaultButton = true
            btn.setOnAction {
                context.showModule(RecordAddModule::class.java, RecordAddForm.Values().also { values ->
                    table.selectionModel.selectedItem.also { volume ->
                        values.googleBookLink(volume.selfLink)
                        values.authors(volume.volumeInfo?.authors?.joinToString(", "))
                        values.date(volume.volumeInfo?.publishedDate)
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
}