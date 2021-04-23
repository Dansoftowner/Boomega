package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.dansoftware.boomega.util.equalsIgnoreCase
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import org.apache.commons.lang3.StringUtils
import org.controlsfx.control.textfield.CustomTextField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors

class RecordFindControl(private val baseItems: ObservableList<Record>) : HBox(5.0) {

    private val onNewResults: ObjectProperty<Consumer<List<Record>>> = SimpleObjectProperty()
    private val onCloseRequest: ObjectProperty<Runnable> = SimpleObjectProperty()
    private val resultsCount: IntegerProperty = SimpleIntegerProperty()
    private val caseSensitive: BooleanProperty = SimpleBooleanProperty()
    private val errorMessage: StringProperty = SimpleStringProperty()

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

    private val baseItemsChangeListener =
        ListChangeListener<Record> {
            search {
                onNewResults.get()?.accept(it)
                resultsCount.set(it.size)
            }
        }

    init {
        styleClass.add("record-find-control")
        padding = Insets(5.0)
        spacing = 5.0
        baseItems.addListener(baseItemsChangeListener)
        buildUI()
    }

    fun releaseListeners() {
        baseItems.removeListener(baseItemsChangeListener)
    }

    private fun buildUI() {
        children.add(buildField())
        ToggleGroup().let {
            children.add(buildRegexToggle(it))
            children.add(buildExactToggle(it))
        }
        children.add(buildCaseToggle())
        children.add(buildSeparator())
        children.add(buildResultsLabel())
        children.add(buildSeparator())
        children.add(buildErrorLabel())
        children.add(buildCloseButton())
    }

    private fun buildField(): TextField = CustomTextField().apply {
        left = StackPane(FontAwesomeIconView(FontAwesomeIcon.SEARCH)).apply {
            padding = Insets(5.0)
        }
        baseText.bind(textProperty())
        prefHeight = 32.0
    }

    private fun buildResultsLabel() = Label().apply {
        textProperty().bind(
            resultsCount.asString().concat(StringUtils.SPACE).concat(I18N.getValue("record.find.results"))
        )
    }.let { StackPane(it) }

    private fun buildErrorLabel() = Label().apply {
        styleClass.add("error-label")
        textProperty().bind(errorMessage)
    }.let { StackPane(it) }

    private fun buildSeparator(): Separator = Separator(Orientation.VERTICAL)

    private fun buildRegexToggle(group: ToggleGroup) =
        ToggleButton(null, MaterialDesignIconView(MaterialDesignIcon.REGEX)).apply {
            tooltip = Tooltip(I18N.getValue("record.find.regex"))
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
            tooltip = Tooltip(I18N.getValue("record.find.exact"))
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
            tooltip = Tooltip(I18N.getValue("record.find.case_sensitive"))
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
            StackPane.setAlignment(this, Pos.CENTER_RIGHT)
        }.let {
            StackPane(it).apply {
                setHgrow(this, Priority.ALWAYS)
            }
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
                setOnRunning {
                    errorMessage.set(null)
                    showProgress()
                }
                setOnFailed {
                    errorMessage.set(null)
                    stopProgress()
                    onItemsAvailable(emptyList())
                    logger.error("Search failed", it.source.exception)
                    when (it.source.exception) {
                        is java.util.regex.PatternSyntaxException ->
                            errorMessage.set(I18N.getValue("record.find.invalid_regex"))
                    }
                }
                setOnSucceeded {
                    errorMessage.set(null)
                    stopProgress()
                    onItemsAvailable(value)
                }
            }

            override fun call(): List<Record> {
                return baseItems.stream()
                    .filter { filter.get().filter(it) }
                    .collect(Collectors.toList())
            }
        }.let(CachedExecutor::submit)
    }

    fun onCloseRequestProperty() = onCloseRequest

    fun setOnCloseRequest(value: Runnable) {
        this.onCloseRequest.set(value)
    }

    fun onNewResultsProperty() = onNewResults

    fun setOnNewResults(value: Consumer<List<Record>>) {
        onNewResults.set(value)
    }

    private abstract class Filter(
        private val baseText: StringProperty,
        private val caseSensitive: BooleanProperty
    ) {

        open val userInputFactory: (String) -> String = { it }
        open val valueFactory: (String) -> String = { it }

        abstract fun checkMatch(
            userInput: String,
            recordValue: String,
            caseSensitive: Boolean
        ): Boolean

        fun filter(record: Record): Boolean =
            record.values().find {
                checkMatch(userInputFactory(baseText.get() ?: ""), valueFactory(it), caseSensitive.get())
            } !== null
    }

    private class SimpleFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {

        override val userInputFactory: (String) -> String = {
            it.let { if (caseSensitive.get().not()) it.toLowerCase() else it }.trim()
        }

        override val valueFactory: (String) -> String = {
            if (caseSensitive.get().not()) it.toLowerCase() else it
        }

        override fun checkMatch(
            userInput: String,
            recordValue: String,
            caseSensitive: Boolean
        ): Boolean = recordValue.contains(userInput)
    }

    private class ExactFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {

        override fun checkMatch(
            userInput: String,
            recordValue: String,
            caseSensitive: Boolean
        ): Boolean =
            if (caseSensitive) recordValue == userInput
            else recordValue.equalsIgnoreCase(userInput)
    }

    private class RegexFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {

        private fun getPattern(userInput: String, caseSensitive: Boolean) =
            if (caseSensitive) Pattern.compile(userInput)
            else Pattern.compile(userInput, Pattern.CASE_INSENSITIVE)

        override fun checkMatch(
            userInput: String,
            recordValue: String,
            caseSensitive: Boolean
        ): Boolean = getPattern(userInput, caseSensitive).matcher(recordValue).matches()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordFindControl::class.java)
    }
}