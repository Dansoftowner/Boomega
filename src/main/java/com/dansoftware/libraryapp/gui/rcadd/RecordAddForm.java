package com.dansoftware.libraryapp.gui.rcadd;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Skinnable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;

public class RecordAddForm extends VBox {

    private final ObjectProperty<RecordType> recordType =
            new SimpleObjectProperty<>() {{
                addListener((observable, oldValue, newValue) -> {
                    var rating = new Rating(5);
                    rating.ratingProperty()
                            .addListener((o, old, newRating) ->
                                    RecordAddForm.this.rating.set((Integer) newRating));
                    switch (newValue) {
                        case BOOK:
                            getChildren().setAll(new FormRenderer(buildBookForm()), rating);
                            break;
                        case MAGAZINE:
                            getChildren().setAll(new FormRenderer(buildMagazineForm()), rating);
                            break;
                    }
                });
            }};

    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty publishedDate = new SimpleStringProperty("");
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

    private Form buildBookForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(authors)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(title)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(isbn)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(language)
                        //TODO: style class
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(publisher)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(subject)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(publishedDate)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofIntegerType(numberOfCopies)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofIntegerType(numberOfPages)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(notes)
                        //TODO: Label
                        //TODO: Place holder

                )
        );
    }

    private Form buildMagazineForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(magazineName)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(title)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(publisher)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(publishedDate)
                        //TODO: Label
                        //TODO: Place holder
                        ,
                        Field.ofStringType(notes)
                        //TODO: Label
                        //TODO: Place holder
                )
        );
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getPublishedDate() {
        return publishedDate.get();
    }

    public StringProperty publishedDateProperty() {
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
