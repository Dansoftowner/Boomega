package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.control.TextFieldLanguageSelectorControl
import com.dansoftware.boomega.gui.record.RecordValues
import com.dansoftware.boomega.gui.control.formsfx.SimpleRatingControl
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.ExploitativeExecutor
import com.dlsc.formsfx.model.structure.Field
import com.dlsc.formsfx.model.structure.Form
import com.dlsc.formsfx.model.structure.Group
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.controls.SimpleTextControl
import com.dlsc.formsfx.view.renderer.FormRenderer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.concurrent.Task
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

class FieldsEditorForm(
    private val context: Context,
    private val database: Database,
) : VBox(5.0) {

    private val itemsCount: IntegerProperty = SimpleIntegerProperty()

    private var items: List<Record> = emptyList()

    private val currentForm: ObjectProperty<Form> = SimpleObjectProperty<Form>()

    private val recordType: ObjectProperty<Record.Type> = object : SimpleObjectProperty<Record.Type>() {
        override fun invalidated() = buildForm(get())
    }

    val onItemsModified: ObjectProperty<Consumer<List<Record>>> = SimpleObjectProperty()

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
        get() = scrollPane.content
        set(value) {
            scrollPane.content = value
        }

    private var bottom: Node
        get() = children[1]
        set(value) {
            children.setAll(scrollPane, value)
        }

    init {
        this.scrollPane = buildScrollPane()
    }

    fun setItems(recordType: Record.Type, items: List<Record>) {
        this.recordType.set(recordType)
        this.items = items

        this.setValues(buildRecordValues(items))
        this.itemsCount.set(items.size)
        this.currentForm.get()
            .changedProperty()
            .or(rating.isNotEqualTo(rating.get()))
            .let(this.changed::bind)
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
            content = it
            bottom = ControlBottom()
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
                    date(items.map(Record::publishedDate).distinct().singleOrNull()?.let(LocalDate::parse))
                    publisher(items.map(Record::publisher).distinct().singleOrNull())
                    magazineName(items.map(Record::magazineName).distinct().singleOrNull())
                    authors(items.map(Record::authors).distinct().singleOrNull()?.joinToString(", "))
                    language(items.map(Record::language).distinct().singleOrNull())
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
            Field.ofStringType(authors)
                .label("record.add.form.authors")
                .placeholder("record.add.form.authors.prompt"),
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt"),
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
            Field.ofIntegerType(rating)
                .label("record.add.form.rating")
                .render(SimpleRatingControl(5, rating))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))

    private fun buildMagazineForm() = Form.of(
        Group.of(
            Field.ofStringType(magazineName)
                .label("record.add.form.magazinename")
                .placeholder("record.add.form.magazinename.prompt"),
            Field.ofStringType(title)
                .label("record.add.form.title")
                .placeholder("record.add.form.title.prompt"),
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
            Field.ofIntegerType(rating)
                .label("record.add.form.rating")
                .render(SimpleRatingControl(5, rating))
        )
    ).i18n(ResourceBundleService(I18N.getValues()))


    private inner class ControlBottom() : VBox(5.0) {
        init {
            this.children.apply {
                add(buildSaveChangesButton())
            }
        }

        private fun buildSaveChangesButton() = Button(I18N.getValue("save.changes")).apply {
            graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE)
            prefWidthProperty().bind(this@FieldsEditorForm.widthProperty())
            disableProperty().bind(this@FieldsEditorForm.changed.not())
            setOnAction {
                //TODO: preview dialog about what items will be changed
                currentForm.get()?.persist()
                ExploitativeExecutor.submit(buildSaveAction())
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
                    this@FieldsEditorForm.apply {
                        StringUtils.getIfBlank(title.get(), null)?.run { record.title = this }
                        StringUtils.getIfBlank(subtitle.get(), null)?.run { record.subtitle = this }
                        StringUtils.getIfBlank(publisher.get(), null)?.run { record.publisher = this }
                        StringUtils.getIfBlank(magazineName.get(), null)?.run { record.magazineName = this }
                        StringUtils.getIfBlank(authors.get(), null)?.run { record.authors = this.split(",") }
                        StringUtils.getIfBlank(language.get(), null)?.run { record.language = this }
                        StringUtils.getIfBlank(isbn.get(), null)?.run { record.isbn = this }
                        StringUtils.getIfBlank(subject.get(), null)?.run { record.subject = this }
                        numberOfCopies.value?.run { record.numberOfCopies = this }
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
        private val logger: Logger = LoggerFactory.getLogger(FieldsEditorForm::class.java)
    }
}