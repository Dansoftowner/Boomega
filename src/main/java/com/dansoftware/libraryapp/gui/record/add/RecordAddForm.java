package com.dansoftware.libraryapp.gui.record.add;

import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.db.data.Magazine;
import com.dansoftware.libraryapp.db.data.ServiceConnection;
import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.googlebooks.SearchParameters;
import com.dansoftware.libraryapp.gui.googlebooks.join.GoogleBookJoinerOverlay;
import com.dansoftware.libraryapp.gui.googlebooks.tile.GoogleBookTile;
import com.dansoftware.libraryapp.gui.record.RecordType;
import com.dansoftware.libraryapp.gui.record.RecordValues;
import com.dansoftware.libraryapp.gui.util.LanguageSelections;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link RecordAddForm} provides a UI for typing Book/Magazine data.
 *
 * <p>
 * It has a {@link RecordType} property that represents what type of database record
 * typed.
 *
 * <p>
 * The {@link #setOnBookAdded(Consumer)} and {@link #setOnMagazineAdded(Consumer)} can be used
 * for listening to commits.
 *
 * @author Daniel Gyorffy
 */
public class RecordAddForm extends ScrollPane {

    private static final Logger logger = LoggerFactory.getLogger(RecordAddForm.class);

    private static final String STYLE_CLASS = "record-add-form";

    private final ObjectProperty<RecordType> recordType = new SimpleObjectProperty<>() {{
        addListener((observable, oldValue, newValue) -> handleTypeChange(newValue));
    }};

    private final ObjectProperty<Consumer<Book>> onBookAdded =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Consumer<Magazine>> onMagazineAdded =
            new SimpleObjectProperty<>();

    private final ObjectProperty<Form> currentForm =
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
    private final IntegerProperty rating = new SimpleIntegerProperty(5);
    private final StringProperty googleBookLink = new SimpleStringProperty();

    private final VBox contentVBox;
    private final Context context;

    private VBox googleBookTileBox;

    public RecordAddForm(@NotNull Context context, @NotNull RecordType initialType) {
        this.context = context;
        this.contentVBox = new VBox();
        this.recordType.set(initialType);
        this.getStyleClass().add(STYLE_CLASS);
        this.buildBaseUI();
    }

    private void buildBaseUI() {
        this.setContent(contentVBox);
        this.setFitToWidth(true);
    }

    private void handleTypeChange(@NotNull RecordType recordType) {
        Form form = null;
        if (recordType == RecordType.BOOK)
            form = buildBookForm();
        else if (recordType == RecordType.MAGAZINE)
            form = buildMagazineForm();
        currentForm.set(form);
        buildFormUI();
    }

    private void buildFormUI() {
        contentVBox.getChildren().setAll(
                renderForm(),
                buildNewRatingControl(),
                buildGoogleBookJoiner(),
                buildCommitButton(contentVBox)
        );
    }

    private Node renderForm() {
        var formRenderer = new FormRenderer(currentForm.get());
        addAutoCompletionToLangField(formRenderer);
        return formRenderer;
    }

    public void setValues(@Nullable RecordValues values) {
        if (values == null) {
            clearForm();
        } else {
            this.recordType.set(values.getRecordType());
            this.title.setValue(values.getTitle());
            this.subtitle.setValue(values.getSubtitle());
            this.publisher.setValue(values.getPublisher());
            this.magazineName.setValue(values.getMagazineName());
            this.authors.setValue(values.getAuthors());
            this.language.setValue(values.getLanguage());
            this.isbn.setValue(values.getIsbn());
            this.subject.setValue(values.getSubject());
            this.notes.setValue(values.getNotes());
            this.numberOfPages.setValue(values.getNumberOfPages());
            this.rating.setValue(values.getRating());
            this.removeGoogleBookConnection();
            this.createGoogleBookConnection(values.getVolumeObject());
            this.publishedDate.setValue(values.getPublishedDate());
        }
    }

    private void addAutoCompletionToLangField(FormRenderer src) {
        SimpleTextControl control = (SimpleTextControl) src.lookup(".languageSelector");
        TextField textField = (TextField) control.lookup(".text-field");
        LanguageSelections.applyOnTextField(context, textField);
    }

    private Node buildNewRatingControl() {
        var rating = new Rating(5);
        StackPane.setAlignment(rating, Pos.CENTER_LEFT);
        rating.setRating(this.rating.get());
        rating.ratingProperty().addListener((o, old, newRating) -> this.rating.set(newRating.intValue()));
        var hBox = new HBox(
                5,
                new StackPane(new Label(I18N.getRecordsValue("record.add.form.rating"))),
                new StackPane(rating) {{
                    HBox.setHgrow(this, Priority.ALWAYS);
                }}
        );
        VBox.setMargin(hBox, new Insets(0, 0, 0, 40));
        return hBox;
    }

    private Node buildGoogleBookJoiner() {
        var vBox = new VBox(5);
        vBox.getChildren().add(buildGoogleBookButton(vBox));
        this.googleBookTileBox = vBox;
        return vBox;
    }

    private Button buildGoogleBookButton(VBox vBox) {
        var button = new Button(I18N.getRecordsValue("record.add.form.gjoin"),
                new MaterialDesignIconView(MaterialDesignIcon.GOOGLE));
        button.setOnAction(event ->
                context.showOverlay(new GoogleBookJoinerOverlay(
                        context, new SearchParameters()
                        .printType(recordType.get().getPrintType())
                        .isbn(isbn.get())
                        .authors(authors.get())
                        .publisher(publisher.get())
                        .title(title.get())
                        .language(language.get()),
                        this::createGoogleBookConnection))
        );
        button.prefWidthProperty().bind(vBox.widthProperty());
        VBox.setMargin(button, new Insets(15, 40, 10, 40));
        return button;
    }

    private Node buildCommitButton(VBox vBox) {
        var button = new Button(
                I18N.getRecordsValue("record.add.form.commit"),
                new MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE));
        button.setDefaultButton(true);
        button.disableProperty().bind(currentForm.get().validProperty().not());
        button.setOnAction(event -> commitData());
        button.prefWidthProperty().bind(vBox.widthProperty());
        VBox.setMargin(button, new Insets(10, 40, 10, 40));
        return button;
    }

    private void commitData() {
        switch (recordType.get()) {
            case BOOK:
                var bookAddAction = onBookAdded.get();
                if (bookAddAction != null) {
                    bookAddAction.accept(buildBookObject());
                    clearForm();
                }
                break;
            case MAGAZINE:
                var magazineAddAction = onMagazineAdded.get();
                if (magazineAddAction != null) {
                    magazineAddAction.accept(buildMagazineObject());
                    clearForm();
                }
                break;
        }
    }

    private void clearForm() {
        title.set("");
        subtitle.set("");
        publishedDate.set(null);
        publisher.set("");
        magazineName.set("");
        authors.set("");
        language.set("");
        isbn.set("");
        subject.set("");
        notes.set("");
        numberOfCopies.setValue(null);
        numberOfPages.setValue(null);
        rating.setValue(null);
        googleBookLink.set(null);
        removeGoogleBookConnection();
    }

    private GoogleBookTile createGoogleBookTile(@NotNull Volume volume) {
        final var googleBookTile = new GoogleBookTile(context, volume, closedVolume -> {
            googleBookLink.set(null);
            googleBookTileBox.getChildren().removeIf(element -> element instanceof GoogleBookTile);
        });
        VBox.setMargin(googleBookTile, new Insets(0, 40, 0, 40));
        return googleBookTile;
    }

    private void createGoogleBookConnection(Volume volume) {
        this.googleBookTileBox.getChildren().removeIf(e -> e instanceof GoogleBookTile);
        this.googleBookLink.setValue(volume.getSelfLink());
        this.googleBookTileBox.getChildren().add(createGoogleBookTile(volume));
    }

    private void removeGoogleBookConnection() {
        googleBookLink.set(null);
        googleBookTileBox.getChildren().removeIf(e -> e instanceof GoogleBookTile);
    }

    private Book buildBookObject() {
        return new Book.Builder()
                .authors(List.of(authors.get().split(",")))
                .isbn(isbn.get())
                .publisher(publisher.get())
                .notes(notes.get())
                .rating(rating.get())
                .subject(subject.get())
                .title(title.get())
                .publishedDate(publishedDate.get())
                .serviceConnection(new ServiceConnection(googleBookLink.get()))
                .build();
    }

    private Magazine buildMagazineObject() {
        return new Magazine.Builder()
                .magazineName(magazineName.get())
                .publisher(publisher.get())
                .title(title.get())
                .publishedDate(publishedDate.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .notes(notes.get())
                .rating(rating.get())
                .language(language.get())
                .serviceConnection(new ServiceConnection(googleBookLink.get()))
                .build();
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
                                .required("record.title.required")
                                .span(ColSpan.HALF),
                        Field.ofStringType(subtitle)
                                .label("record.add.form.subtitle")
                                .placeholder("record.add.form.subtitle.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(isbn)
                                .label("record.add.form.isbn")
                                .placeholder("record.add.form.isbn.prompt")
                                .required(false)
                                .span(ColSpan.HALF),
                        Field.ofStringType(language)
                                .styleClass("languageSelector")
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
                                .format("record.date.error")
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
        ).i18n(new ResourceBundleService(I18N.getRecordsValues()))
                .binding(BindingMode.CONTINUOUS);
    }

    private Form buildMagazineForm() {
        return Form.of(
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
        ).i18n(new ResourceBundleService(I18N.getRecordsValues()))
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

    public String getGoogleBookLink() {
        return googleBookLink.get();
    }

    public StringProperty googleBookLinkProperty() {
        return googleBookLink;
    }

}
