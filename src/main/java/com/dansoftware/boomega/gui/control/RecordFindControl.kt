package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.util.concurrent.ExploitativeExecutor
import com.dansoftware.boomega.util.equalsIgnoreCase
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import org.controlsfx.control.textfield.CustomTextField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors

class RecordFindControl(private val baseItems: List<Record>) : HBox(5.0) {

    private val onNewResults: ObjectProperty<Consumer<List<Record>>> = SimpleObjectProperty()
    private val onCloseRequest: ObjectProperty<Runnable> = SimpleObjectProperty()
    private val resultsCount: IntegerProperty = SimpleIntegerProperty()
    private val caseSensitive: BooleanProperty = SimpleBooleanProperty()

    private val baseText: SimpleStringProperty =
        object : SimpleStringProperty() {
            override fun invalidated() {
                search {
                    onNewResults.get()?.accept(it)
                    resultsCount.set(it.size)
                }
            }
        }

    private val filter: ObjectProperty<Filter> =
        object : SimpleObjectProperty<Filter>(SimpleFilter(baseText, caseSensitive)) {
            override fun invalidated() {
                search {
                    onNewResults.get()?.accept(it)
                    resultsCount.set(it.size)
                }
            }
        }

    init {
        padding = Insets(5.0)
        spacing = 5.0
        buildUI()
    }

    private fun buildUI() {
        CustomTextField().apply {
            left = StackPane(FontAwesomeIconView(FontAwesomeIcon.SEARCH))
            baseText.bind(textProperty())
        }.let(children::add)

        ToggleGroup().let {
            children.add(buildRegexToggle(it))
            children.add(buildExactToggle(it))
        }
        children.add(buildCaseToggle())

        children.add(Separator(Orientation.VERTICAL))
        Label().apply {
            textProperty().bind(resultsCount.asString().concat(" results")) //TODO: i18n suffix
        }.let { StackPane(it) }.let(children::add)

        children.add(buildCloseButton().let {
            StackPane.setAlignment(it, Pos.CENTER_RIGHT)
            StackPane(it).apply { setHgrow(this, Priority.ALWAYS) }
        })
    }

    private fun buildRegexToggle(group: ToggleGroup) =
        ToggleButton(null, MaterialDesignIconView(MaterialDesignIcon.REGEX)).apply {
            //TODO: tooltip
            toggleGroup = group
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            selectedProperty().addListener { _, _, newValue ->
                filter.set(
                    if (newValue)
                        RegexFilter(baseText, caseSensitive)
                    else
                        SimpleFilter(baseText, caseSensitive)
                )
            }
        }

    private fun buildExactToggle(group: ToggleGroup) =
        ToggleButton(null, MaterialDesignIconView(MaterialDesignIcon.KEYBOARD)).apply {
            //TODO: tooltip
            toggleGroup = group
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            selectedProperty().addListener { _, _, newValue ->
                filter.set(
                    if (newValue)
                        ExactFilter(baseText, caseSensitive)
                    else
                        SimpleFilter(baseText, caseSensitive)
                )
            }
        }

    private fun buildCaseToggle() =
        ToggleButton(null, MaterialDesignIconView(MaterialDesignIcon.CASE_SENSITIVE_ALT)).apply {
            //TODO: tooltip
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            caseSensitive.bind(selectedProperty())
            selectedProperty().addListener { _, _, _ ->
                filter.get().also { oldFilter ->
                    filter.set(
                        when (oldFilter) {
                            is ExactFilter -> ExactFilter(baseText, caseSensitive)
                            is RegexFilter -> RegexFilter(baseText, caseSensitive)
                            else -> SimpleFilter(baseText, caseSensitive)
                        }
                    )
                }
            }
        }

    private fun buildCloseButton() =
        Button(null, MaterialDesignIconView(MaterialDesignIcon.CLOSE)).apply {
            padding = Insets(0.0)
            setOnAction {
                onCloseRequest.get()?.run()
            }
            visibleProperty().bind(onCloseRequest.isNotNull)
        }

    private fun showProgress() {
        ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS).apply {
            prefWidth = 15.0
            prefHeight = 15.0
        }.let(children::add)
    }

    private fun stopProgress() {
        children.removeIf { it is ProgressIndicator }
    }

    private fun search(onItemsAvailable: (List<Record>) -> Unit) {
        object : Task<List<Record>>() {

            init {
                setOnRunning { showProgress() }
                setOnFailed {
                    stopProgress()
                    logger.error("Search failed", it.source.exception)
                    //TODO: error dialog
                }
                setOnSucceeded {
                    stopProgress()
                    onItemsAvailable(value)
                }
            }

            override fun call(): List<Record> {
                return baseItems.stream()
                    .filter { filter.get().filter(it) }
                    .collect(Collectors.toList())
            }
        }.let(ExploitativeExecutor::submit)
    }

    fun onCloseRequestProperty() = onCloseRequest

    fun setOnCloseRequest(value: Runnable) {
        this.onCloseRequest.set(value)
    }

    fun onNewResultsProperty() = onNewResults

    fun setOnNewResults(value: Consumer<List<Record>>) {
        onNewResults.set(value)
    }

    private abstract class Filter {

        abstract fun checkSingleValueMatches(value: String): Boolean

        fun filter(record: Record): Boolean =
            getRecordValues(record).find { checkSingleValueMatches(it) } !== null

        private fun getRecordValues(record: Record): List<String> {
            return listOfNotNull(
                record.title,
                record.isbn,
                record.language,
                record.magazineName,
                record.notes,
                record.publishedDate,
                record.publisher,
                record.subject,
                record.subtitle,
                *record.authors?.toTypedArray() ?: emptyArray()
            )
        }
    }

    private class SimpleFilter(
        private val baseText: StringProperty,
        private val caseSensitive: BooleanProperty
    ) : Filter() {

        override fun checkSingleValueMatches(value: String): Boolean =
            getValidBaseText()?.let { baseText -> getValidValue(value).contains(baseText) } ?: false

        private fun getValidBaseText() = baseText.get()?.let { if (caseSensitive.get().not()) it.toLowerCase() else it }

        private fun getValidValue(value: String) = value.let { if (caseSensitive.get().not()) it.toLowerCase() else it }
    }

    private class ExactFilter(
        private val baseText: StringProperty,
        private val caseSensitive: BooleanProperty
    ) : Filter() {

        override fun checkSingleValueMatches(value: String): Boolean =
            if (caseSensitive.get()) value == baseText.get()
            else value.equalsIgnoreCase(baseText.get())

    }

    private class RegexFilter(
        private val baseText: StringProperty,
        private val caseSensitive: BooleanProperty
    ) : Filter() {

        override fun checkSingleValueMatches(value: String): Boolean =
            getPattern().matcher(value).matches()

        private fun getPattern() =
            if (caseSensitive.get()) Pattern.compile(baseText.get())
            else Pattern.compile(baseText.get(), Pattern.CASE_INSENSITIVE)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordFindControl::class.java)
    }
}