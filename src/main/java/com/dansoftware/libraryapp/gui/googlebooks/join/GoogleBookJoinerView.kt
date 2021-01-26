package com.dansoftware.libraryapp.gui.googlebooks.join

import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.context.TitledOverlayBox
import com.dansoftware.libraryapp.gui.googlebooks.*
import com.dansoftware.libraryapp.i18n.I18N
import com.dansoftware.libraryapp.util.ExploitativeExecutor
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass
import java.util.function.Consumer

class GoogleBookJoinerOverlay(
    context: Context,
    searchParameters: SearchParameters,
    onVolumeSelected: Consumer<Volume>
) : TitledOverlayBox(
    I18N.getGoogleBooksImportValue("google.books.joiner.titlebar"),
    ImageView(Image("/com/dansoftware/libraryapp/image/util/google_12px.png")),
    GoogleBookJoinerView(
        context,
        searchParameters
    ) { context.hideOverlay(it.parent?.parent?.parent?.parent as Region?) } //TODO: not so good solution
        .also { it.setOnVolumeSelected(onVolumeSelected) }
)

class GoogleBookJoinerView(
    private val context: Context,
    private val searchParameters: SearchParameters,
    private val hideMethod: (GoogleBookJoinerView) -> Unit
) : VBox(5.0) {

    private val onVolumeSelected: ObjectProperty<Consumer<Volume>> = SimpleObjectProperty()

    private val allParameters: SearchParameters = searchParameters.copy()

    private val propertyChooserPane: PropertyChooserPane
    private val tablePagination: GoogleBooksPagination

    init {
        setVgrow(this, Priority.ALWAYS)
        styleClass.add("google-books-joiner-panel")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        propertyChooserPane = buildPropertyChooserPane()
        tablePagination = buildTable()
        children.add(StackPane(propertyChooserPane))
        children.add(tablePagination)
        propertyChooserPane.monitor()
    }

    private fun buildPropertyChooserPane() = PropertyChooserPane(allParameters, searchParameters) {
        ExploitativeExecutor.submit(
            GoogleBooksPaginationSearchTask(context, tablePagination, true, searchParameters)
        )
    }

    private fun buildTable() =
        GoogleBooksPagination().also {
            VBox.setVgrow(it, Priority.ALWAYS)
            it.table.setOnItemDoubleClicked { volume ->
                onVolumeSelected.get()?.accept(volume)
                hideMethod(this)
            }
            it.table.setOnItemSecondaryDoubleClicked { volume ->
                context.showOverlay(
                    GoogleBookDetailsOverlay(
                        context,
                        volume
                    )
                )
            }
            it.table.addColumns(
                GoogleBooksTable.ColumnType.INDEX_COLUMN,
                GoogleBooksTable.ColumnType.TYPE_INDICATOR_COLUMN,
                GoogleBooksTable.ColumnType.THUMBNAIL_COLUMN,
                GoogleBooksTable.ColumnType.AUTHOR_COLUMN,
                GoogleBooksTable.ColumnType.TITLE_COLUMN,
            )
        }

    fun setOnVolumeSelected(onVolumeSelected: Consumer<Volume>) {
        this.onVolumeSelected.set(onVolumeSelected)
    }

    private class PropertyChooserPane(
        private val allParameters: SearchParameters,
        private val searchParameters: SearchParameters,
        val monitor: () -> Unit
    ) : HBox(5.0) {

        private lateinit var titleSelected: BooleanProperty
        private lateinit var authorsSelected: BooleanProperty
        private lateinit var publisherSelected: BooleanProperty
        private lateinit var isbnSelected: BooleanProperty
        private lateinit var languageSelected: BooleanProperty

        private lateinit var maxResults: ReadOnlyObjectProperty<Int>
        private lateinit var validProperty: BooleanBinding

        init {
            this.buildUI()
            this.buildMonitoringPolicy()
        }

        private fun buildMonitoringPolicy() =
            listOf(titleSelected, authorsSelected, publisherSelected, isbnSelected, languageSelected, maxResults)
                .forEach {
                    it.addListener { _, _, _ -> monitor() }
                }


        private fun buildValidProperty() = titleSelected
            .or(authorsSelected)
            .or(publisherSelected)
            .or(isbnSelected)

        private fun buildUI() {
            padding = Insets(5.0)
            children.add(buildLeftBox())
            validProperty = buildValidProperty()
            children.add(buildRightBox())
        }

        private fun buildLeftBox(): VBox =
            VBox(3.0).also { vBox ->
                setHgrow(vBox, Priority.ALWAYS)
                vBox.children.add(CheckBox(I18N.getGoogleBooksImportValue("google.books.joiner.isbn")).also { checkBox ->
                    checkBox.isSelected = true
                    this.isbnSelected = checkBox.selectedProperty().also {
                        it.addListener { _, _, yes ->
                            searchParameters.isbn(if (yes) allParameters.isbn else null)
                        }
                    }
                })

                vBox.children.add(CheckBox(I18N.getGoogleBooksImportValue("google.books.joiner.title")).also { checkBox ->
                    checkBox.isSelected = true
                    this.titleSelected = checkBox.selectedProperty().also {
                        it.addListener { _, _, yes ->
                            searchParameters.title(if (yes) allParameters.title else null)
                        }
                    }
                })

                vBox.children.add(CheckBox(I18N.getGoogleBooksImportValue("google.books.joiner.authors")).also {
                    it.isSelected = true
                    this.authorsSelected = it.selectedProperty().also { checkBox ->
                        checkBox.addListener { _, _, yes ->
                            searchParameters.authors(if (yes) allParameters.authors else null)
                        }
                    }
                })

                vBox.children.add(CheckBox(I18N.getGoogleBooksImportValue("google.books.joiner.publisher")).also { checkBox ->
                    this.publisherSelected = checkBox.selectedProperty().also {
                        it.addListener { _, _, yes ->
                            searchParameters.publisher(if (yes) allParameters.publisher else null)
                        }
                    }
                })
            }

        private fun buildRightBox(): VBox =
            VBox(3.0).also { vBox ->
                setHgrow(vBox, Priority.ALWAYS)
                vBox.children.add(CheckBox(I18N.getGoogleBooksImportValue("google.books.joiner.language")).also { checkBox ->
                    this.languageSelected = checkBox.selectedProperty().also {
                        it.addListener { _, _, yes ->
                            searchParameters.language(if (yes) allParameters.language else null)
                        }
                    }
                    checkBox.disableProperty().bind(this.validProperty.not())
                })

                vBox.children.add(Label(I18N.getGoogleBooksImportValue("google.books.joiner.maxresults")))
                vBox.children.add(Spinner<Int>(1, 40, 10).also { spinner ->
                    this.maxResults = spinner.valueProperty().also {
                        it.addListener { _, _, newValue ->
                            searchParameters.maxResults(newValue)
                        }
                    }
                })
            }

    }
}


