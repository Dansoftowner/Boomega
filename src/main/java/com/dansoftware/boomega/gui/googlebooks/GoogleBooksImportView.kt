package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.record.RecordValues
import com.dansoftware.boomega.gui.record.add.RecordAddModule
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.stream.Collectors

/**
 * A [GoogleBooksImportView] is a [GoogleBooksSearchView] that has an additional button
 * on the bottom that calls the [RecordAddModule] for importing the selected volume.
 *
 * @author Daniel Gyorffy
 */
class GoogleBooksImportView(
    private val context: Context,
    private val preferences: Preferences
) : BorderPane() {

    companion object {
        private val colConfigKey =
            Preferences.Key(
                "google.books.module.table.columns",
                TableColumnsInfo::class.java,
                TableColumnsInfo::byDefault
            )
        private val abcConfigKey =
            Preferences.Key("google.books.module.table.abcsort", Locale::class.java, Locale::getDefault)
    }

    private val toolBar = buildToolBar()
    private val searchView: GoogleBooksSearchView = buildContent()

    val table: GoogleBooksTable
        get() = searchView.table

    init {
        top = toolBar
        center = searchView
        readABCConfigurations()
        readColumnConfigurations()
    }

    fun refresh() = searchView.refresh()
    fun scrollToTop() = searchView.scrollToTop()
    fun clear() = searchView.clear()

    fun writeConfig() {
        preferences.editor().apply {
            put(colConfigKey, TableColumnsInfo(table.showingColumns))
            put(abcConfigKey, toolBar.abcLocaleProperty().get())
        }
    }

    private fun readColumnConfigurations() {
        preferences[colConfigKey].columnTypes.forEach(searchView.table::addColumn)
        toolBar.updateColumnChooser()
    }

    private fun readABCConfigurations() {
        toolBar.abcLocaleProperty().set(preferences.get(abcConfigKey))
    }

    private fun buildToolBar() = GoogleBooksImportToolBar(this)

    private fun buildContent() = object : GoogleBooksSearchView(context) {

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
                    SimpleStringProperty(I18N.getValue("google.books.import.button"))
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
            context.showModule(RecordAddModule::class.java, RecordValues()
                .apply {
                    table.selectionModel.selectedItem.also { volume ->
                        recordType(
                            when {
                                volume.volumeInfo?.isMagazine ?: false -> Record.Type.MAGAZINE
                                else -> Record.Type.BOOK
                            }
                        )
                        authors(volume.volumeInfo?.authors?.joinToString(", "))
                        date(volume.volumeInfo?.getPublishedDateObject())
                        isbn(volume.volumeInfo?.industryIdentifiers?.find { it.isIsbn13 }?.identifier)
                        language(volume.volumeInfo?.language)
                        title(volume.volumeInfo?.title)
                        subtitle(volume.volumeInfo?.subtitle)
                        notes(volume.volumeInfo?.description)
                        publisher(volume.volumeInfo?.publisher)
                        googleVolumeObject(volume)
                    }
                })
        }
    }

    /**
     * Used for storing the preferred table columns in the configurations.
     */
    class TableColumnsInfo(val columnTypes: List<GoogleBooksTable.ColumnType>) {
        companion object {
            fun byDefault(): TableColumnsInfo {
                return TableColumnsInfo(
                    Arrays.stream(GoogleBooksTable.ColumnType.values())
                        .filter { it.isDefaultVisible }
                        .collect(Collectors.toList())
                )
            }
        }
    }
}