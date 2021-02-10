package com.dansoftware.libraryapp.gui.record.show.dock.editor

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.record.RecordValues
import com.dansoftware.libraryapp.gui.util.FixedFontMaterialDesignIconView
import com.dansoftware.libraryapp.gui.util.applyOnTextField
import com.dansoftware.libraryapp.gui.util.formsfx.SimpleRatingControl
import com.dansoftware.libraryapp.i18n.I18N
import com.dansoftware.libraryapp.util.concurrent.ExploitativeExecutor
import com.dlsc.formsfx.model.structure.*
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.controls.SimpleTextControl
import com.dlsc.formsfx.view.renderer.FormRenderer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordEditor(
    private val context: Context,
    private val database: Database,
    items: List<Record>
) : VBox(5.0) {

    private val scrollPane: ScrollPane

    private val recordType: ObjectProperty<Record.Type?> = object : SimpleObjectProperty<Record.Type?>() {
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
    private val rating: IntegerProperty = SimpleIntegerProperty()

    private val itemsCount: IntegerProperty = SimpleIntegerProperty(0)
    private val multipleItems: BooleanProperty = SimpleBooleanProperty().apply {
        bind(itemsCount.greaterThan(1))
    }

    var items: List<Record> = emptyList()
        set(value) {
            field = value
            itemsCount.set(items.size)
            recordType.set(value.map(Record::recordType).distinct().singleOrNull())
            recordValues.set(buildRecordValues(value))
        }

    init {
        this.styleClass.add("record-editor")
        this.scrollPane = buildScrollPane()
        this.buildBaseUI()
        this.items = items
    }

    private fun showProgress() {
        ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS).let {
            children.add(0, it)
        }
    }

    private fun stopProgress() {
        children.removeIf { it is ProgressBar }
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
                    date(items.map(Record::publishedDate).distinct().singleOrNull()?.let(LocalDate::parse))
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
        setVgrow(this, Priority.ALWAYS)
    }


    private fun buildBaseUI() {
        this.children.add(scrollPane)
        buildBottom().also {
            itemsCount.addListener { _, _, newCount ->
                if (newCount == 0) children.remove(it)
                else children.add(it)
            }
        }
    }

    private fun buildBottom() = ControlBottom()

    private fun handleNewRecord(recordValues: RecordValues?) {
        recordValues?.also {
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
            currentForm.get()?.persist()
        } ?: clearForm()
    }

    private fun handleTypeChange(recordType: Record.Type?) {
        logger.debug("changed record type: $recordType")
        currentForm.set(
            when (recordType) {
                Record.Type.BOOK -> buildBookForm()
                Record.Type.MAGAZINE -> buildMagazineForm()
                else -> null
            }
        )
        buildFormUI()
    }

    private fun buildFormUI() {
        setContent(renderForm()?.let {
            VBox(
                buildTypeIndicator(),
                Separator().apply { setMargin(this, Insets(10.0, 20.0, 0.0, 20.0)) },
                it
            )
        } ?: MultipleSelectionPlaceHolder())
    }

    private fun buildTypeIndicator() = HBox(5.0).also { hBox ->
        hBox.children.apply {
            add(
                FixedFontMaterialDesignIconView(
                    when (recordType.get()) {
                        Record.Type.MAGAZINE -> MaterialDesignIcon.NEWSPAPER
                        else -> MaterialDesignIcon.BOOK
                    }, 25.0
                )
            )
            add(
                Label(
                    I18N.getValue(
                        when (recordType.get()) {
                            Record.Type.MAGAZINE -> "google.books.magazine"
                            else -> "google.books.book"
                        }
                    )
                ).apply { styleClass.add("medium-font") }
                    .let { StackPane(it.apply { StackPane.setAlignment(this, Pos.CENTER_LEFT) }) }
            )
        }
        VBox.setMargin(hBox, Insets(20.0, 0.0, 0.0, 20.0))
    }

    private fun renderForm() = currentForm.get()?.let {
        FormRenderer(it).apply(::addAutoCompletionToLangField)
    }

    private fun buildBookForm() = Form.of(
        Group.of(
            Field.ofStringType(authors)
                .label("record.add.form.authors")
                .placeholder("record.add.form.authors.prompt")
                .required(false),
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt")
                .required("record.title.required")
                .apply { multipleItems.addListener { _, _, it -> this.requiredProperty().set(it.not()) } },
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
                .multiline(true),
            Field.ofIntegerType(rating)
                .label("record.add.form.rating")
                .render(SimpleRatingControl(5))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))

    private fun buildMagazineForm() = Form.of(
        Group.of(
            Field.ofStringType(magazineName)
                .label("record.add.form.magazinename")
                .placeholder("record.add.form.magazinename.prompt")
                .required("record.magazinename.required")
                .apply { multipleItems.addListener { _, _, it -> this.requiredProperty().set(it.not()) } },
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt")
                .required("record.title.required")
                .apply { multipleItems.addListener { _, _, it -> this.requiredProperty().set(it.not()) } },
            Field.ofStringType(publisher)
                .label("record.add.form.publisher")
                .placeholder("record.add.form.publisher.prompt")
                .required(false),
            Field.ofDate(publishedDate)
                .label("record.add.form.date")
                .placeholder("record.add.form.date.prompt")
                .required(false)
                .format("record.date.error"),
            Field.ofStringType(language)
                .styleClass("languageSelector")
                .label("record.add.form.lang")
                .placeholder("record.add.form.lang.prompt")
                .required(false),
            Field.ofStringType(notes)
                .label("record.add.form.notes")
                .placeholder("record.add.form.notes.prompt")
                .required(false)
                .multiline(true),
            Field.ofIntegerType(rating)
                .label("record.add.form.rating")
                .render(SimpleRatingControl(5))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))

    private fun addAutoCompletionToLangField(src: FormRenderer) {
        src.lookup(".languageSelector").let {
            it as SimpleTextControl
        }.let {
            it.lookup(".text-field") as TextField
        }.also {
            applyOnTextField(context, it)
        }
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

    private inner class ControlBottom() : VBox(5.0) {
        init {
            this.children.apply {
                add(buildSaveChangesButton())
                add(buildRemoveButton())
            }
        }

        private fun buildSaveChangesButton() = Button(I18N.getValue("save.changes")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE)
            prefWidthProperty().bind(this@RecordEditor.widthProperty())
            setOnAction {
                //TODO: preview dialog about what items will be changed
                ExploitativeExecutor.submit(buildSaveAction())
            }
        }

        private fun buildRemoveButton() = Button(I18N.getValue("record.remove")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.DELETE)
            styleClass.add("remove-button")
            prefWidthProperty().bind(this@RecordEditor.widthProperty())
            setOnAction {
                //TODO: showing what items will be removed 'Are you sure'
                ExploitativeExecutor.submit(buildRemoveAction())
            }
        }

        private fun buildRemoveAction() = object : Task<Unit>() {
            init {
                setOnRunning { showProgress() }
                setOnSucceeded {
                    stopProgress()
                    currentForm.get()?.persist()
                    //TODO: showing success notification
                    //TODO: update editor and table
                }
                setOnFailed {
                    stopProgress()
                    //TODO: ALERT DIALOG
                }
            }

            override fun call() {
                items.forEach(database::removeRecord)
            }
        }

        private fun buildSaveAction() = object : Task<Unit>() {
            init {
                setOnRunning { showProgress() }
                setOnSucceeded {
                    stopProgress()
                    currentForm.get()?.persist()
                    //TODO: showing success notification
                }
                setOnFailed {
                    stopProgress()
                    //TODO: ALERT DIALOG
                }
            }

            override fun call() {
                items.forEach {
                    this@RecordEditor.apply {
                        title.get()?.run { it.title = this }
                        subtitle.get()?.run { it.subtitle = this }
                        publishedDate.get()
                            ?.run { it.publishedDate = this.format(DateTimeFormatter.ofPattern("yyyy-MM-mm")) }
                        publisher.get()?.run { it.publisher = this }
                        magazineName.get()?.run { it.magazineName = this }
                        authors.get()?.run { it.authors = this.split(",") }
                        language.get()?.run { it.language = this }
                        isbn.get()?.run { it.isbn = this }
                        subject.get()?.run { it.subject = this }
                        notes.get()?.run { it.notes = this }
                        numberOfCopies.value?.run { it.numberOfCopies = this }
                        numberOfPages.value?.run { it.numberOfPages = this }
                        rating.value?.run { it.rating = this }
                    }
                    database.updateRecord(it)
                }
            }

        }

    }

    private class MultipleRecordTypePlaceHolder : StackPane() {

    }

    private class MultipleSelectionPlaceHolder : StackPane() {

    }

    private class EmptyPlaceHolder : StackPane() {

    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RecordEditor::class.java)
    }
}