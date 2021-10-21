/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class RecordFindControl(private val baseItems: ObservableList<Record>) : HBox(5.0) {

    @get:JvmName("onNewResultsProperty")
    val onNewResultsProperty: ObjectProperty<(List<Record>) -> Unit> = SimpleObjectProperty()

    @get:JvmName("onCloseRequestProperty")
    val onCloseRequestProperty: ObjectProperty<() -> Unit> = SimpleObjectProperty()

    private val resultsCount: IntegerProperty = SimpleIntegerProperty()
    private val caseSensitive: BooleanProperty = SimpleBooleanProperty()
    private val errorMessage: StringProperty = SimpleStringProperty()

    private val baseText: SimpleStringProperty =
        object : SimpleStringProperty() {
            override fun invalidated() {
                search {
                    onNewResults?.invoke(it)
                    resultsCount.set(it.size)
                }
            }
        }

    var onNewResults: ((List<Record>) -> Unit)?
        get() = onNewResultsProperty.get()
        set(value) {
            onNewResultsProperty.set(value)
        }

    var onCloseRequest: (() -> Unit)?
        get() = onCloseRequestProperty.get()
        set(value) {
            onCloseRequestProperty.set(value)
        }

    private val filter: ObjectProperty<Filter> =
        object : SimpleObjectProperty<Filter>(SimpleFilter(baseText, caseSensitive)) {
            override fun invalidated() {
                search {
                    onNewResults?.invoke(it)
                    resultsCount.set(it.size)
                }
            }
        }

    private val baseItemsChangeListener =
        ListChangeListener<Record> {
            search {
                onNewResults?.invoke(it)
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

    override fun requestFocus() {
        // requesting focus on the text-field
        children[0]!!.requestFocus()
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

    private fun buildField(): TextField = SearchTextField().apply {
        baseText.bind(textProperty())
        prefHeight = 32.0
    }

    private fun buildResultsLabel() = Label().apply {
        textProperty().bind(
            resultsCount.asString().concat(" ").concat(I18N.getValue("record.find.results"))
        )
    }.let { StackPane(it) }

    private fun buildErrorLabel() = Label().apply {
        styleClass.add("error-label")
        textProperty().bind(errorMessage)
    }.let { StackPane(it) }

    private fun buildSeparator(): Separator = Separator(Orientation.VERTICAL)

    private fun buildRegexToggle(group: ToggleGroup) =
        ToggleButton(null, icon("regex-icon")).apply {
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
        ToggleButton(null, icon("keyboard-icon")).apply {
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
        ToggleButton(null, icon("case-sensitive-icon")).apply {
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
        Button(null, icon("close-icon")).apply {
            padding = Insets(0.0)
            setOnAction {
                onCloseRequest?.invoke()
            }
            visibleProperty().bind(onCloseRequestProperty.isNotNull)
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

    private abstract class Filter(
        private val baseText: StringProperty,
        private val caseSensitive: BooleanProperty
    ) {
        abstract fun checkMatch(userInput: String, recordValue: String, ignoreCase: Boolean): Boolean

        fun filter(record: Record): Boolean =
            record.values().find { checkMatch(baseText.get() ?: "", it, !caseSensitive.get()) } !== null
    }

    private class SimpleFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {
        override fun checkMatch(userInput: String, recordValue: String, ignoreCase: Boolean): Boolean =
            recordValue.contains(userInput.trim(), ignoreCase)
    }

    private class ExactFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {
        override fun checkMatch(userInput: String, recordValue: String, ignoreCase: Boolean): Boolean =
            recordValue.equals(userInput, ignoreCase)
    }

    private class RegexFilter(
        baseText: StringProperty,
        caseSensitive: BooleanProperty
    ) : Filter(baseText, caseSensitive) {

        private fun compileRegex(userInput: String, ignoreCase: Boolean) =
            Regex(userInput, if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet())

        override fun checkMatch(userInput: String, recordValue: String, ignoreCase: Boolean): Boolean =
            compileRegex(userInput, ignoreCase).matches(recordValue)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordFindControl::class.java)
    }
}