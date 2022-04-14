/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.recordview.edit

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.TextFieldLanguageSelectorControl
import com.dansoftware.boomega.gui.control.formsfx.SimpleRatingControl
import com.dansoftware.boomega.gui.recordview.RecordValues
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.nullIfBlank
import com.dlsc.formsfx.model.structure.Field
import com.dlsc.formsfx.model.structure.Form
import com.dlsc.formsfx.model.structure.Group
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.controls.SimpleTextControl
import com.dlsc.formsfx.view.renderer.FormRenderer
import javafx.beans.property.*
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class FieldsEditorForm(
    private val context: Context,
    private val database: Database,
) : VBox(5.0) {

    private val currentForm: ObjectProperty<Form> = SimpleObjectProperty()
    private val itemsCount: IntegerProperty = SimpleIntegerProperty()
    private var items: List<Record> = emptyList()

    private val recordType: ObjectProperty<Record.Type> = object : SimpleObjectProperty<Record.Type>() {
        override fun invalidated() = buildForm(get())
    }

    private val changed: BooleanProperty = SimpleBooleanProperty(false)

    private val title: StringProperty = SimpleStringProperty("")
    private val subtitle: StringProperty = SimpleStringProperty("")
    private val publishedDate: ObjectProperty<LocalDate> = SimpleObjectProperty()
    private val publisher: StringProperty = SimpleStringProperty("")
    private val magazineName: StringProperty = SimpleStringProperty("")
    private val authors: StringProperty = SimpleStringProperty("")
    private val language: StringProperty = SimpleStringProperty("")
    private val isbn: StringProperty = SimpleStringProperty("")
    private val subject: StringProperty = SimpleStringProperty("")
    private val numberOfCopies: IntegerProperty = SimpleIntegerProperty(1)
    private val rating: IntegerProperty = SimpleIntegerProperty()

    private val scrollPane: ScrollPane

    private var content: Node
        inline get() = scrollPane.content
        inline set(value) {
            scrollPane.content = value
        }

    init {
        this.scrollPane = buildScrollPane()
    }

    fun setItems(recordType: Record.Type, items: List<Record>) {
        this.recordType.set(recordType)
        this.items = items

        this.setValues(buildRecordValues(items))
        this.itemsCount.set(items.size)
        this.updateChangedProperty()
    }

    /**
     * Should be executed after saving the modifications or after new items are set
     */
    fun updateChangedProperty() {
        changed.bind(
            this.currentForm.get()
                .changedProperty()
                .or(rating.isNotEqualTo(rating.get()))
        )
    }

    private fun buildScrollPane() = ScrollPane().also {
        setVgrow(it, Priority.ALWAYS)
        it.isFitToWidth = true
        children.add(0, it)
    }

    private fun buildForm(recordType: Record.Type) {
        currentForm.set(
            when (recordType) {
                Record.Type.BOOK -> buildBookForm()
                Record.Type.MAGAZINE -> buildMagazineForm()
            }
        )
        buildFormUI()
    }

    private fun buildFormUI() {
        renderForm()?.let {
            content = it
        }
    }

    private fun renderForm() = currentForm.get()?.let {
        FormRenderer(it).apply(::addAutoCompletionToLangField)
    }

    private fun buildRecordValues(items: List<Record>): RecordValues? =
        when {
            items.isNotEmpty() -> {
                RecordValues().apply {
                    title(items.map(Record::title).distinct().singleOrNull())
                    subtitle(items.map(Record::subtitle).distinct().singleOrNull())
                    date(items.map(Record::publishedDate).distinct().singleOrNull())
                    publisher(items.map(Record::publisher).distinct().singleOrNull())
                    magazineName(items.map(Record::magazineName).distinct().singleOrNull())
                    authors(items.map(Record::authors).distinct().singleOrNull()?.joinToString(", "))
                    language(items.map { it.language?.language }.distinct().singleOrNull())
                    isbn(items.map(Record::isbn).distinct().singleOrNull())
                    subject(items.map(Record::subject).distinct().singleOrNull())
                    notes(items.map(Record::notes).distinct().singleOrNull())
                    numberOfCopies(items.map(Record::numberOfCopies).distinct().singleOrNull())
                    rating(items.map(Record::rating).distinct().singleOrNull())
                }
            }
            else -> null
        }

    private fun setValues(values: RecordValues?) {
        values?.also {
            title.value = it.title
            subtitle.value = it.subtitle
            publisher.value = it.publisher
            magazineName.value = it.magazineName
            authors.value = it.authors
            language.value = it.language
            isbn.value = it.isbn
            subject.value = it.subject
            numberOfCopies.value = it.numberOfCopies
            rating.value = it.rating
            publishedDate.value = it.publishedDate
            currentForm.get()?.persist()
        } ?: clearForm()
    }

    private fun clearForm() {
        title.set("")
        subtitle.set("")
        publishedDate.set(null)
        publisher.set("")
        magazineName.set("")
        authors.set("")
        language.set("")
        isbn.set("")
        subject.set("")
        numberOfCopies.value = null
        rating.value = null
    }

    private fun addAutoCompletionToLangField(src: FormRenderer) {
        src.lookup(".languageSelector").let {
            it as SimpleTextControl
        }.let {
            it.lookup(".text-field") as TextField
        }.also {
            TextFieldLanguageSelectorControl.applyOnTextField(context, it)
        }
    }

    private fun buildBookForm() = Form.of(
        Group.of(
            Field.ofSingleSelectionType(Record.Type.values().asList(), 0)
                .apply { recordType.bindBidirectional(selectionProperty()) }
                .label("${i18n("record.property.type")}:"),
            Field.ofStringType(authors)
                .label("${i18n("record.property.authors")}:"),
            Field.ofStringType(title)
                .label("${i18n("record.property.title")}:"),
            Field.ofStringType(subtitle)
                .label("${i18n("record.property.subtitle")}:")
                .required(false),
            Field.ofStringType(isbn)
                .label("${i18n("record.property.isbn")}:")
                .required(false),
            Field.ofStringType(language)
                .styleClass("languageSelector")
                .label("${i18n("record.property.lang")}:")
                .placeholder(i18n("record.add.form.lang.prompt"))
                .required(false),
            Field.ofStringType(publisher)
                .label("${i18n("record.property.publisher")}:")
                .required(false),
            Field.ofStringType(subject)
                .label("${i18n("record.property.subject")}:")
                .required(false),
            Field.ofDate(publishedDate)
                .label("${i18n("record.property.published_date")}:")
                .required(false)
                .format("record.date.error"),
            Field.ofIntegerType(numberOfCopies)
                .label("${i18n("record.property.nofcopies")}:")
                .required(false),
            Field.ofIntegerType(rating)
                .label("${i18n("record.property.rating")}:")
                .render(SimpleRatingControl(5, rating))
        )
    )

    private fun buildMagazineForm() = Form.of(
        Group.of(
            Field.ofSingleSelectionType(Record.Type.values().asList(), 1)
                .apply { recordType.bindBidirectional(selectionProperty()) }
                .label("record.property.type"),
            Field.ofStringType(magazineName)
                .label("record.property.magazinename")
                .placeholder("record.add.form.magazinename.prompt"),
            Field.ofStringType(title)
                .label("record.property.title")
                .placeholder("record.add.form.title.prompt"),
            Field.ofStringType(publisher)
                .label("record.property.publisher")
                .placeholder("record.add.form.publisher.prompt")
                .required(false),
            Field.ofDate(publishedDate)
                .label("record.property.published_date")
                .placeholder("record.add.form.date.prompt")
                .required(false)
                .format("record.date.error"),
            Field.ofStringType(language)
                .styleClass("languageSelector")
                .label("record.property.lang")
                .placeholder("record.add.form.lang.prompt")
                .required(false),
            Field.ofIntegerType(rating)
                .label("record.property.rating")
                .render(SimpleRatingControl(5, rating))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))

    fun saveChanges() {
        logger.debug("FieldsEditorForm.saveChanges() invoked")
        logger.debug("Count of items that will be modified: {}", items.size)
        items.forEach { record ->
            logger.debug("Item will be modified: ${record.id}")
            record.type = recordType.get()
            record.title = title.get().nullIfBlank()
            record.subtitle = subtitle.get().nullIfBlank()
            record.publisher = publisher.get().nullIfBlank()
            record.magazineName = magazineName.get().nullIfBlank()
            record.authors = authors.get().nullIfBlank()?.split(",")
            record.language = language.get().nullIfBlank()?.let(Locale::forLanguageTag)
            record.isbn = isbn.get().nullIfBlank()
            record.subject = subject.get().nullIfBlank()
            record.numberOfCopies = numberOfCopies.value
            record.rating = rating.value
            record.publishedDate = publishedDate.get()
        }
        logger.debug("Updating ({}) records in database...", items.size)
        items.forEach(database::updateRecord)
    }

    /**
     * Action that should be executed before saving the modifications
     */
    fun persist() {
        currentForm.get()?.persist()
    }

    fun changedProperty(): BooleanProperty = changed

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FieldsEditorForm::class.java)
    }
}