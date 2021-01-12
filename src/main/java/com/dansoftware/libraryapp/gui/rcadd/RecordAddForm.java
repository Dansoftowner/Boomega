package com.dansoftware.libraryapp.gui.rcadd;

import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.Magazine;
import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.gui.googlebooks.join.GoogleBookJoinerOverlay;
import com.dansoftware.libraryapp.gui.googlebooks.join.GoogleBookJoinerPanel;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.function.Consumer;

public class RecordAddForm extends VBox {

    private static final String STYLE_CLASS = "record-add-form";

    private final ObjectProperty<RecordType> recordType = new SimpleObjectProperty<>() {{
        addListener((observable, oldValue, newValue) -> handleTypeChange(newValue));
    }};

    private final ObjectProperty<Consumer<Book>> onBookAdded =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Consumer<Magazine>> onMagazineAdded =
            new SimpleObjectProperty<>();

    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty subtitle = new SimpleStringProperty("");
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

    private final Context context;

    public RecordAddForm(@NotNull Context context, @NotNull RecordType initialType) {
        this.context = context;
        this.recordType.set(initialType);
        this.getStyleClass().add(STYLE_CLASS);
    }

    private void handleTypeChange(@NotNull RecordType recordType) {
        switch (recordType) {
            case BOOK:
                getChildren().setAll(new FormRenderer(buildBookForm()), buildNewRatingControl(), buildGoogleBooksJoiner());
                break;
            case MAGAZINE:
                getChildren().setAll(new FormRenderer(buildMagazineForm()), buildNewRatingControl(), buildGoogleBooksJoiner());
                break;
        }
    }

    private Node buildNewRatingControl() {
        var rating = new Rating(5);
        rating.ratingProperty()
                .addListener((o, old, newRating) -> this.rating.set(newRating.intValue()));
        var hBox = new HBox(
                5,
                new StackPane(new Label(I18N.getRecordAddFormValue("record.add.form.rating"))),
                new StackPane(rating) {{ HBox.setHgrow(this, Priority.ALWAYS); }}
        );
        VBox.setMargin(hBox, new Insets(0, 0, 0, 40));
        return hBox;
    }

    private Node buildGoogleBooksJoiner() {
        var button = new Button(I18N.getRecordAddFormValue("record.add.form.gjoin"), new MaterialDesignIconView(MaterialDesignIcon.GOOGLE));
        var hBox = new HBox(
                5,
                button
        );
        button.setOnAction(event -> {
            context.showOverlay(new GoogleBookJoinerOverlay(
                    context, new SearchParameters()
                                .printType(recordType.get().printType)
                                .isbn(isbn.get())
                                .authors(authors.get())
                                .publisher(publisher.get())
                                .title(title.get())
                                .language(language.get())
            ));
        });
        VBox.setMargin(hBox, new Insets(0, 0, 0, 40));
        return hBox;
    }

    private Form buildBookForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(authors)
                                .label("record.add.form.authors")
                                .placeholder("record.add.form.authors.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(title)
                                .label("record.add.form.title")
                                .placeholder("record.add.form.title.prompt")
                                .required(true)
                                .span(ColSpan.HALF),
                        Field.ofStringType(subtitle)
                                .label("record.add.form.subtitle")
                                .placeholder("record.add.form.subtitle.prompt")
                                .required(true)
                                .span(ColSpan.HALF),
                        Field.ofStringType(isbn)
                                .label("record.add.form.isbn")
                                .placeholder("record.add.form.isbn.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(language)
                                .label("record.add.form.lang")
                                .placeholder("record.add.form.lang.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(publisher)
                                .label("record.add.form.publisher")
                                .placeholder("record.add.form.publisher.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(subject)
                                .label("record.add.form.subject")
                                .placeholder("record.add.form.subject.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofDate(publishedDate)
                                .label("record.add.form.date")
                                .placeholder("record.add.form.date.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofIntegerType(numberOfCopies)
                                .label("record.add.form.nofcopies")
                                .required(false)
                                .placeholder("record.add.form.nofcopies.prompt")
                                .span(ColSpan.HALF),
                        Field.ofIntegerType(numberOfPages)
                                .label("record.add.form.nofpages")
                                .placeholder("record.add.form.nofpages.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(notes)
                                .label("record.add.form.notes")
                                .placeholder("record.add.form.notes.prompt")
                                .required(false)
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
                                .required(true)
                                .span(ColSpan.HALF),
                        Field.ofStringType(title)
                                .label("record.add.form.title")
                                .placeholder("record.add.form.title.prompt")
                                .required(true)
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
                                .span(ColSpan.HALF),
                        Field.ofStringType(notes)
                                .label("record.add.form.notes")
                                .placeholder("record.add.form.notes.prompt")
                                .required(false)
                                .multiline(true)
                )
        ).i18n(new ResourceBundleService(I18N.getRecordAddFormValues()))
                .binding(BindingMode.CONTINUOUS);
    }

    public Consumer<Book> getOnBookAdded() {
        return onBookAdded.get();
    }

    public ObjectProperty<Consumer<Book>> onBookAddedProperty() {
        return onBookAdded;
    }

    public void setOnBookAdded(Consumer<Book> onBookAdded) {
        this.onBookAdded.set(onBookAdded);
    }

    public Consumer<Magazine> getOnMagazineAdded() {
        return onMagazineAdded.get();
    }

    public ObjectProperty<Consumer<Magazine>> onMagazineAddedProperty() {
        return onMagazineAdded;
    }

    public void setOnMagazineAdded(Consumer<Magazine> onMagazineAdded) {
        this.onMagazineAdded.set(onMagazineAdded);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getSubtitle() {
        return subtitle.get();
    }

    public StringProperty subtitleProperty() {
        return subtitle;
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
        BOOK(GoogleBooksQueryBuilder.PrintType.BOOKS), MAGAZINE(GoogleBooksQueryBuilder.PrintType.MAGAZINES);

        private GoogleBooksQueryBuilder.PrintType printType;

        RecordType(GoogleBooksQueryBuilder.PrintType printType) {
            this.printType = printType;
        }
    }
}
