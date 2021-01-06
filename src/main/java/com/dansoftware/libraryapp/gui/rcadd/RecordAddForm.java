package com.dansoftware.libraryapp.gui.rcadd;

import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import javafx.beans.property.*;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class RecordAddForm extends VBox {

    private final ObjectProperty<RecordType> recordType = new SimpleObjectProperty<>() {{
        addListener((observable, oldValue, newValue) -> handleTypeChange(newValue));
    }};

    private final StringProperty title = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> publishedDate = new SimpleObjectProperty<>();
    private final StringProperty publisher = new SimpleStringProperty("");
    private final StringProperty magazineName = new SimpleStringProperty("");
    private final StringProperty authors = new SimpleStringProperty("");
    private final StringProperty language = new SimpleStringProperty("");
    private final StringProperty isbn = new SimpleStringProperty("");
    private final StringProperty subject = new SimpleStringProperty("");
    private final StringProperty notes = new SimpleStringProperty("");
    private final IntegerProperty numberOfCopies = new SimpleIntegerProperty(1);
    private final IntegerProperty numberOfPages = new SimpleIntegerProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();


    public RecordAddForm(@NotNull RecordType initialType) {
        this.recordType.set(initialType);
    }

    private void handleTypeChange(@NotNull RecordType recordType) {
        switch (recordType) {
            case BOOK:
                getChildren().setAll(new FormRenderer(buildBookForm()), buildNewRatingControl());
                break;
            case MAGAZINE:
                getChildren().setAll(new FormRenderer(buildMagazineForm()), buildNewRatingControl());
                break;
        }
    }

    private Rating buildNewRatingControl() {
        var rating = new Rating(5);
        rating.ratingProperty()
                .addListener((o, old, newRating) -> this.rating.set(newRating.intValue()));
        return rating;
    }

    private Form buildBookForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(authors)
                                .label("record.add.form.authors")
                                .placeholder("record.add.form.authors.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(title)
                                .label("record.add.form.title")
                                .placeholder("record.add.form.title.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(isbn)
                                .label("record.add.form.isbn")
                                .placeholder("record.add.form.isbn.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(language)
                                .label("record.add.form.lang")
                                .placeholder("record.add.form.lang.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(publisher)
                                .label("record.add.form.publisher")
                                .placeholder("record.add.form.publisher.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(subject)
                                .label("record.add.form.subject")
                                .placeholder("record.add.form.subject.prompt")
                                .span(ColSpan.HALF),
                        Field.ofDate(publishedDate)
                                .label("record.add.form.date")
                                .placeholder("record.add.form.date.prompt")
                                .span(ColSpan.HALF),
                        Field.ofIntegerType(numberOfCopies)
                                .label("record.add.form.nofcopies")
                                .placeholder("record.add.form.nofcopies.prompt")
                                .span(ColSpan.HALF),
                        Field.ofIntegerType(numberOfPages)
                                .label("record.add.form.nofpages")
                                .placeholder("record.add.form.nofpages.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(notes)
                                .label("record.add.form.notes")
                                .placeholder("record.add.form.notes.prompt")
                                .multiline(true)

                )
        ).i18n(new ResourceBundleService(I18N.getRecordAddFormValues()))
                .binding(BindingMode.CONTINUOUS);
    }

    private Form buildMagazineForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(magazineName)
                                .label("record.add.form.magazinename")
                                .placeholder("record.add.form.magazinename.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(title)
                                .label("record.add.form.title")
                                .placeholder("record.add.form.title.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(publisher)
                                .label("record.add.form.publisher")
                                .placeholder("record.add.form.publisher.prompt")
                                .span(ColSpan.HALF),
                        Field.ofDate(publishedDate)
                                .label("record.add.form.date")
                                .placeholder("record.add.form.date.prompt")
                                .span(ColSpan.HALF),
                        Field.ofStringType(notes)
                                .label("record.add.form.notes")
                                .placeholder("record.add.form.notes.prompt")
                                .multiline(true)
                )
        ).i18n(new ResourceBundleService(I18N.getRecordAddFormValues()))
                .binding(BindingMode.CONTINUOUS);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public LocalDate getPublishedDate() {
        return publishedDate.get();
    }

    public ObjectProperty<LocalDate> publishedDateProperty() {
        return publishedDate;
    }

    public String getPublisher() {
        return publisher.get();
    }

    public StringProperty publisherProperty() {
        return publisher;
    }

    public String getMagazineName() {
        return magazineName.get();
    }

    public StringProperty magazineNameProperty() {
        return magazineName;
    }

    public String getAuthors() {
        return authors.get();
    }

    public StringProperty authorsProperty() {
        return authors;
    }

    public String getLanguage() {
        return language.get();
    }

    public StringProperty languageProperty() {
        return language;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public int getNumberOfCopies() {
        return numberOfCopies.get();
    }

    public IntegerProperty numberOfCopiesProperty() {
        return numberOfCopies;
    }

    public int getNumberOfPages() {
        return numberOfPages.get();
    }

    public IntegerProperty numberOfPagesProperty() {
        return numberOfPages;
    }

    public int getRating() {
        return rating.get();
    }

    public IntegerProperty ratingProperty() {
        return rating;
    }

    public RecordType getRecordType() {
        return recordType.get();
    }

    public ObjectProperty<RecordType> recordTypeProperty() {
        return recordType;
    }

    public enum RecordType {
        BOOK, MAGAZINE
    }
}
