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
import com.dlsc.formsfx.model.structure.Field
import com.dlsc.formsfx.model.structure.Form
import com.dlsc.formsfx.model.structure.Group
import com.dlsc.formsfx.model.util.BindingMode
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
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

class RecordEditorForm(
    private val context: Context,
    private val database: Database,
) : VBox(5.0) {

    private val itemsCount: IntegerProperty = SimpleIntegerProperty()

    private val multipleItems: BooleanProperty = SimpleBooleanProperty().apply {
        bind(itemsCount.greaterThan(1))
    }

    private var items: List<Record> = emptyList()

    private val currentForm: ObjectProperty<Form> = SimpleObjectProperty<Form>()

    private val recordType: ObjectProperty<Record.Type> = object : SimpleObjectProperty<Record.Type>() {
        override fun invalidated() = buildForm(get())
    }

    val onItemsModified: ObjectProperty<Consumer<List<Record>>> = SimpleObjectProperty()
    val onItemsDeleted: ObjectProperty<Consumer<List<Record>>> = SimpleObjectProperty()

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
    private val notes: StringProperty = SimpleStringProperty("")
    private val numberOfCopies: IntegerProperty = SimpleIntegerProperty(1)
    private val numberOfPages: IntegerProperty = SimpleIntegerProperty()
    private val rating: IntegerProperty = SimpleIntegerProperty()

    private val scrollPane: ScrollPane

    private var content: Node
        get() = scrollPane.content
        set(value) {
            scrollPane.content = value
        }

    private var bottom: Node
        get() = children[1]
        set(value) = children.setAll(scrollPane, value).let { Unit }

    init {
        this.scrollPane = buildScrollPane()
    }

    fun setItems(recordType: Record.Type, items: List<Record>) {
        this.recordType.set(recordType)
        this.items = items

        this.setValues(buildRecordValues(items))
        this.itemsCount.set(items.size)
        this.changed.bind(currentForm.get().changedProperty().or(rating.isNotEqualTo(rating.get())))
    }

    private fun buildScrollPane() = ScrollPane().also {
        setVgrow(it, Priority.ALWAYS)
        it.isFitToWidth = true
        children.add(0, it)
    }

    private fun showProgress() {
        ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS).let {
            children.add(0, it)
        }
    }

    private fun stopProgress() {
        children.removeIf { it is ProgressBar }
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
            content = VBox(
                buildTypeIndicator(),
                Separator().apply { setMargin(this, Insets(10.0, 20.0, 0.0, 20.0)) },
                it
            )
            bottom = ControlBottom()
        }
    }

    private fun renderForm() = currentForm.get()?.let {
        FormRenderer(it).apply(::addAutoCompletionToLangField)
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

    private fun buildRecordValues(items: List<Record>): RecordValues? =
        when {
            items.isNotEmpty() -> {
                RecordValues().apply {
                    title(items.map(Record::title).distinct().singleOrNull())
                    subtitle(items.map(Record::subtitle).distinct().singleOrNull())
                    date(items.map(Record::publishedDate).distinct().singleOrNull()?.let(LocalDate::parse))
                    publisher(items.map(Record::publisher).distinct().singleOrNull())
                    magazineName(items.map(Record::magazineName).distinct().singleOrNull())
                    authors(items.map(Record::authors).distinct().singleOrNull()?.joinToString(", "))
                    language(items.map(Record::language).distinct().singleOrNull())
                    isbn(items.map(Record::isbn).distinct().singleOrNull())
                    subject(items.map(Record::subject).distinct().singleOrNull())
                    notes(items.map(Record::notes).distinct().singleOrNull())
                    numberOfCopies(items.map(Record::numberOfCopies).distinct().singleOrNull())
                    numberOfPages(items.map(Record::numberOfPages).distinct().singleOrNull())
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
            notes.value = it.notes
            numberOfCopies.value = it.numberOfCopies
            numberOfPages.value = it.numberOfPages
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
        notes.set("")
        numberOfCopies.value = null
        numberOfPages.value = null
        rating.value = null
    }

    private fun addAutoCompletionToLangField(src: FormRenderer) {
        src.lookup(".languageSelector").let {
            it as SimpleTextControl
        }.let {
            it.lookup(".text-field") as TextField
        }.also {
            applyOnTextField(context, it)
        }
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
                .render(SimpleRatingControl(5, rating))
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
                .render(SimpleRatingControl(5, rating))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))


    private inner class ControlBottom() : VBox(5.0) {
        init {
            this.children.apply {
                add(buildSaveChangesButton())
                add(buildRemoveButton())
            }
        }

        private fun buildSaveChangesButton() = Button(I18N.getValue("save.changes")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE)
            prefWidthProperty().bind(this@RecordEditorForm.widthProperty())
            disableProperty().bind(this@RecordEditorForm.changed.not())
            setOnAction {
                //TODO: preview dialog about what items will be changed
                currentForm.get()?.persist()
                ExploitativeExecutor.submit(buildSaveAction())
            }
        }

        private fun buildRemoveButton() = Button(I18N.getValue("record.remove")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.DELETE)
            styleClass.add("remove-button")
            prefWidthProperty().bind(this@RecordEditorForm.widthProperty())
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
                    onItemsDeleted.get()?.accept(items)
                    //TODO: showing success notification
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
                    onItemsModified.get()?.accept(items)
                    //TODO: showing success notification
                }
                setOnFailed {
                    stopProgress()
                    logger.error("Something went wrong", it.source.exception)
                    //TODO: ALERT DIALOG
                }
            }

            @Suppress("DuplicatedCode")
            override fun call() {
                items.forEach { record ->
                    this@RecordEditorForm.apply {
                        StringUtils.getIfBlank(title.get(), null)?.run { record.title = this }
                        StringUtils.getIfBlank(subtitle.get(), null)?.run { record.subtitle = this }
                        StringUtils.getIfBlank(publisher.get(), null)?.run { record.publisher = this }
                        StringUtils.getIfBlank(magazineName.get(), null)?.run { record.magazineName = this }
                        StringUtils.getIfBlank(authors.get(), null)?.run { record.authors = this.split(",") }
                        StringUtils.getIfBlank(language.get(), null)?.run { record.language = this }
                        StringUtils.getIfBlank(isbn.get(), null)?.run { record.isbn = this }
                        StringUtils.getIfBlank(subject.get(), null)?.run { record.subject = this }
                        StringUtils.getIfBlank(notes.get(), null)?.run { record.notes = this }
                        numberOfCopies.value?.run { record.numberOfCopies = this }
                        numberOfPages.value?.run { record.numberOfPages = this }
                        rating.value?.run { record.rating = this }
                        publishedDate.get()
                            ?.run {
                                try {
                                    record.publishedDate = this.format(DateTimeFormatter.ofPattern("yyyy-MM-mm"))
                                } catch (e : RuntimeException) {
                                    logger.error("Couldn't parse date ", e)
                                }
                            }
                    }
                }
                logger.debug("Updating ({}) records in database...", items.size)
                items.forEach(database::updateRecord)
            }

        }

    }
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordEditorForm::class.java)
    }
}