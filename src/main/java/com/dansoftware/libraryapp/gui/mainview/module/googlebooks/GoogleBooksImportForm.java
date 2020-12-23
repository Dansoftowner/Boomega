package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

class GoogleBooksImportForm {

    private final Form form;

    private final ResourceBundle resourceBundle;

    private StringProperty generalText;
    private StringProperty author;
    private StringProperty title;
    private StringProperty publisher;
    private StringProperty isbn;
    private StringProperty language;
    private ObservableList<Filter> selectableFilters;

    private ObjectProperty<Filter> filter;
    private ObservableList<SortType> selectableSorts;

    private ObjectProperty<SortType> sort;
    private IntegerProperty maxResults;

    GoogleBooksImportForm(@NotNull ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.initProperties();
        this.form = buildForm();
    }

    private void initProperties() {
        generalText = new SimpleStringProperty("");
        author = new SimpleStringProperty("");
        title = new SimpleStringProperty("");
        publisher = new SimpleStringProperty("");
        isbn = new SimpleStringProperty("");
        language = new SimpleStringProperty("");

        selectableFilters = FXCollections.observableArrayList(
                new Filter(GoogleBooksQueryBuilder.PrintType.ALL, resourceBundle, "google.books.add.form.filter.all"),
                new Filter(GoogleBooksQueryBuilder.PrintType.BOOKS, resourceBundle, "google.books.add.form.filter.books"),
                new Filter(GoogleBooksQueryBuilder.PrintType.MAGAZINES, resourceBundle, "google.books.add.form.filter.magazines")
        );
        filter = new SimpleObjectProperty<>(selectableFilters.get(0));

        selectableSorts = FXCollections.observableArrayList(
                new SortType(GoogleBooksQueryBuilder.SortType.REVELANCE, resourceBundle, "google.books.add.form.sort.relevance"),
                new SortType(GoogleBooksQueryBuilder.SortType.NEWEST, resourceBundle, "google.books.add.form.sort.newest")
        );
        sort = new SimpleObjectProperty<>(selectableSorts.get(0));
        maxResults = new SimpleIntegerProperty(10);
    }

    private Form buildForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(generalText)
                                .placeholder("google.books.add.form.gtext.prompt")
                                .label("google.books.add.form.gtext"),
                        Field.ofStringType(author)
                                .placeholder("google.books.add.form.author.prompt")
                                .label("google.books.add.form.author")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(title)
                                .placeholder("google.books.add.form.title.prompt")
                                .label("google.books.add.form.title")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(publisher)
                                .placeholder("google.books.add.form.publisher.prompt")
                                .label("google.books.add.form.publisher")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(isbn)
                                .placeholder("google.books.add.form.isbn.prompt")
                                .label("google.books.add.form.isbn")
                                .span(ColSpan.TWO_THIRD),
                        Field.ofStringType(language)
                                .placeholder("google.books.add.form.lang.prompt")
                                .label("google.books.add.form.lang")
                                .span(ColSpan.THIRD),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(selectableFilters), filter)
                                .label("google.books.add.form.filter")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(selectableSorts), sort)
                                .label("google.books.add.form.sort")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofIntegerType(maxResults)
                                .label("google.books.add.form.maxresults")
                                .validate(IntegerRangeValidator.between(1, 40, "google.books.add.form.maxresults.incorrect"))
                )
        ).title("google.books.add.form.ftitle").binding(BindingMode.CONTINUOUS).i18n(new ResourceBundleService(resourceBundle));
    }

    public boolean isValid() {
        return form.isValid();
    }

    public FormRenderer getRenderer() {
        return new FormRenderer(this.form);
    }

    public String getGeneralText() {
        return generalText.get();
    }

    public StringProperty generalTextProperty() {
        return generalText;
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getPublisher() {
        return publisher.get();
    }

    public StringProperty publisherProperty() {
        return publisher;
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public String getLanguage() {
        return language.get();
    }

    public StringProperty languageProperty() {
        return language;
    }

    public Filter getFilter() {
        return filter.get();
    }

    public ObjectProperty<Filter> filterProperty() {
        return filter;
    }

    public SortType getSort() {
        return sort.get();
    }

    public ObjectProperty<SortType> sortProperty() {
        return sort;
    }

    public int getMaxResults() {
        return maxResults.get();
    }

    public IntegerProperty maxResultsProperty() {
        return maxResults;
    }

    static class SortType {

        private final GoogleBooksQueryBuilder.SortType type;

        private final ResourceBundle resourceBundle;
        private final String i18nKey;

        SortType(GoogleBooksQueryBuilder.SortType type, ResourceBundle resourceBundle, String i18nKey) {
            this.type = type;
            this.resourceBundle = resourceBundle;
            this.i18nKey = i18nKey;
        }

        public GoogleBooksQueryBuilder.SortType getType() {
            return type;
        }

        @Override
        public String toString() {
            return resourceBundle.getString(i18nKey);
        }

    }

    static class Filter {

        private final GoogleBooksQueryBuilder.PrintType type;
        private final ResourceBundle resourceBundle;
        private final String i18nKey;

        Filter(GoogleBooksQueryBuilder.PrintType type, ResourceBundle resourceBundle, String i18nKey) {
            this.type = type;
            this.resourceBundle = resourceBundle;
            this.i18nKey = i18nKey;
        }

        public GoogleBooksQueryBuilder.PrintType getType() {
            return type;
        }

        @Override
        public String toString() {
            return resourceBundle.getString(i18nKey);
        }
    }

}
