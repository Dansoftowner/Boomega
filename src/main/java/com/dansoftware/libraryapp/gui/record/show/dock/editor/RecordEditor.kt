package com.dansoftware.libraryapp.gui.record.show.dock.editor

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.record.RecordValues
import com.dansoftware.libraryapp.gui.util.applyOnTextField
import com.dansoftware.libraryapp.i18n.I18N
import com.dlsc.formsfx.model.structure.Field
import com.dlsc.formsfx.model.structure.Form
import com.dlsc.formsfx.model.structure.Group
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.controls.SimpleTextControl
import com.dlsc.formsfx.view.renderer.FormRenderer
import com.dlsc.formsfx.view.util.ColSpan
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.controlsfx.control.Rating
import java.time.LocalDate

class RecordEditor(
    private val context: Context,
    private val database: Database,
    items: List<Record>
) : VBox(5.0) {

    private val scrollPane: ScrollPane

    private val recordType: ObjectProperty<Record.Type> = object : SimpleObjectProperty<Record.Type>() {
        override fun invalidated() {
            handleTypeChange(get())
        }
    }

    private val recordValues: ObjectProperty<RecordValues?> = object : SimpleObjectProperty<RecordValues?>() {
        override fun invalidated() {
            handleNewRecord(get())
        }
    }

    private val currentForm: ObjectProperty<Form> = SimpleObjectProperty()

    private val title: StringProperty = SimpleStringProperty("")
    private val subtitle: StringProperty = SimpleStringProperty("")
    private val publishedDate: ObjectProperty<LocalDate> = SimpleObjectProperty()
    private val publisher: StringProperty = SimpleStringProperty("")
    private val magazineName: StringProperty = SimpleStringProperty("")
    private val authors: StringProperty = SimpleStringProperty("")
    private val language: StringProperty = SimpleStringProperty("")
    private val isbn: StringProperty = SimpleStringProperty("")
    private val subject: StringProperty = SimpleStringProperty("")
    private val notes: StringProperty = SimpleStringProperty("")
    private val numberOfCopies: IntegerProperty = SimpleIntegerProperty(1)
    private val numberOfPages: IntegerProperty = SimpleIntegerProperty()
    private val rating: IntegerProperty = SimpleIntegerProperty(5)

    var items: List<Record> = emptyList()
    set(value) {
        field = value
        recordValues.set(buildRecordValues(value))
    }

    init {
        this.scrollPane = buildScrollPane()
        this.buildBaseUI()
        this.items = items
    }

    private fun setContent(content: Node) {
        scrollPane.content = content
    }

    private fun buildRecordValues(items: List<Record>) =
        when {
            items.isNotEmpty() -> {
                RecordValues().apply {
                    title(items.map(Record::title).distinct().singleOrNull())
                    subtitle(items.map(Record::subtitle).distinct().singleOrNull())
                    date(items.map(Record::publishedDate).distinct().singleOrNull()?.let { LocalDate.parse(it) })
                    publisher(items.map(Record::publisher).distinct().singleOrNull())
                    magazineName(items.map(Record::magazineName).distinct().singleOrNull())
                    authors(items.map(Record::authors).singleOrNull()?.joinToString(", "))
                    language(items.map(Record::language).singleOrNull())
                    isbn(items.map(Record::isbn).singleOrNull())
                    subject(items.map(Record::subject).singleOrNull())
                    notes(items.map(Record::notes).singleOrNull())
                    numberOfCopies(items.map(Record::numberOfCopies).singleOrNull())
                    numberOfPages(items.map(Record::numberOfPages).singleOrNull())
                    rating(items.map(Record::rating).singleOrNull())
                }
            }
            else -> null
        }

    private fun buildScrollPane() = ScrollPane().apply {
        this.isFitToWidth = true
        VBox.setVgrow(this, Priority.ALWAYS)
    }


    private fun buildBaseUI() {
        this.children.add(scrollPane)
    }

    private fun buildBottom() {

    }

    private fun handleNewRecord(recordValues: RecordValues?) {
        recordValues?.also {
            recordType.set(it.recordType)
            title.value = it.title
            subtitle.value = it.subtitle
            publisher.value = it.publisher
            magazineName.value = it.magazineName
            authors.value = it.authors
            language.value = it.language
            isbn.value = it.isbn
            subject.value = it.subject
            notes.value = it.notes
            numberOfPages.value = it.numberOfPages
            rating.value = it.rating
            publishedDate.value = it.publishedDate
            currentForm.get().persist()
        } ?: clearForm()
    }

    private fun handleTypeChange(recordType: Record.Type) {
        currentForm.set(when(recordType) {
            Record.Type.BOOK -> buildBookForm()
            Record.Type.MAGAZINE -> buildMagazineForm()
        })
        buildFormUI()
    }

    private fun buildFormUI() {
        setContent(VBox(5.0, buildTypeIndicator(), renderForm(), buildNewRatingControl()))
    }

    private fun buildTypeIndicator() = HBox(5.0).also { hBox ->
        when(recordType.get()) {
            Record.Type.MAGAZINE -> {
                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.NEWSPAPER).apply {
                    glyphSize = 25
                })
                hBox.children.add(Label(I18N.getValue("google.books.magazine")))
            }
            else -> {
                hBox.children.add(MaterialDesignIconView(MaterialDesignIcon.BOOK).apply { glyphSize = 25 })
                hBox.children.add(Label(I18N.getValue("google.books.book")))
            }
        }
    }

    private fun renderForm() = FormRenderer(currentForm.get()).apply(::addAutoCompletionToLangField)

    private fun buildBookForm() = Form.of(
        Group.of(
            Field.ofStringType(authors)
                .label("record.add.form.authors")
                .placeholder("record.add.form.authors.prompt")
                .required(false),
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt")
                .required("record.title.required"),
            Field.ofStringType(subtitle)
                .label("record.add.form.subtitle")
                .placeholder("record.add.form.subtitle.prompt")
                .required(false),
            Field.ofStringType(isbn)
                .label("record.add.form.isbn")
                .placeholder("record.add.form.isbn.prompt")
                .required(false),
            Field.ofStringType(language)
                .styleClass("languageSelector")
                .label("record.add.form.lang")
                .placeholder("record.add.form.lang.prompt")
                .required(false),
            Field.ofStringType(publisher)
                .label("record.add.form.publisher")
                .placeholder("record.add.form.publisher.prompt")
                .required(false),
            Field.ofStringType(subject)
                .label("record.add.form.subject")
                .placeholder("record.add.form.subject.prompt")
                .required(false),
            Field.ofDate(publishedDate)
                .label("record.add.form.date")
                .placeholder("record.add.form.date.prompt")
                .required(false)
                .format("record.date.error"),
            Field.ofIntegerType(numberOfCopies)
                .label("record.add.form.nofcopies")
                .required(false)
                .placeholder("record.add.form.nofcopies.prompt"),
            Field.ofIntegerType(numberOfPages)
                .label("record.add.form.nofpages")
                .placeholder("record.add.form.nofpages.prompt")
                .required(false),
            Field.ofStringType(notes)
                .label("record.add.form.notes")
                .placeholder("record.add.form.notes.prompt")
                .required(false)
                .multiline(true)
        )
    ).i18n(ResourceBundleService(I18N.getValues()))

    private fun buildMagazineForm() = Form.of(
        Group.of(
            Field.ofStringType(magazineName)
                .label("record.add.form.magazinename")
                .placeholder("record.add.form.magazinename.prompt")
                .required("record.magazinename.required")
                .span(ColSpan.HALF),
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt")
                .required("record.title.required")
                .span(ColSpan.HALF),
            Field.ofStringType(publisher)
                .label("record.add.form.publisher")
                .placeholder("record.add.form.publisher.prompt")
                .required(false)
                .span(ColSpan.HALF),
            Field.ofDate(publishedDate)
                .label("record.add.form.date")
                .placeholder("record.add.form.date.prompt")
                .required(false)
                .format("record.date.error")
                .span(ColSpan.HALF),
            Field.ofStringType(language)
                .styleClass("languageSelector")
                .label("record.add.form.lang")
                .placeholder("record.add.form.lang.prompt")
                .required(false)
                .span(ColSpan.HALF),
            Field.ofStringType(notes)
                .label("record.add.form.notes")
                .placeholder("record.add.form.notes.prompt")
                .required(false)
                .multiline(true)
        )
    ).i18n( ResourceBundleService(I18N.getValues()))

    private fun buildNewRatingControl(): Node =
        HBox(
            5.0,
            StackPane(Label(I18N.getValue("record.add.form.rating"))),
            StackPane(Rating(5).also {
                StackPane.setAlignment(it, Pos.CENTER_LEFT)
                it.rating = this.rating.get().toDouble()
                it.ratingProperty().addListener { _, _, newRating: Number -> this.rating.set(newRating.toInt()) }
            }).apply { HBox.setHgrow(this, Priority.ALWAYS) }
        ).apply { setMargin(this, Insets(0.0, 0.0, 0.0, 40.0)) }

    private fun addAutoCompletionToLangField(src: FormRenderer) {
        val control = src.lookup(".languageSelector") as SimpleTextControl
        val textField = control.lookup(".text-field") as TextField
        applyOnTextField(context, textField)
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
        notes.set("")
        numberOfCopies.value = null
        numberOfPages.value = null
        rating.value = null
    }

    private class MultipleSelectionPlaceHolder : StackPane() {

    }

    private class EmptyPlaceHolder : StackPane() {

    }
}