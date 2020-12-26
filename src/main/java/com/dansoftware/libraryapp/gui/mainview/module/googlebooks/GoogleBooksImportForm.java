package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder;
import com.dansoftware.libraryapp.gui.util.LanguageSelection;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.dlsc.formsfx.view.util.ColSpan;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;
import java.util.function.Consumer;

class GoogleBooksImportForm extends TitledPane {

    private static final String STYLE_CLASS = "google-books-import-form";

    private final Context context;
    private final SearchData searchData;
    private final Form form;

    GoogleBooksImportForm(@NotNull Context context, @NotNull Consumer<SearchData> onSearch) {
        super(I18N.getGoogleBooksImportValue("google.books.add.form.section.title"), null);
        this.context = context;
        this.searchData = new SearchData(new SimpleBooleanProperty());
        this.form = buildForm();
        this.searchData.validProperty().bind(form.validProperty());
        this.setContent(buildContent(onSearch));
        this.getStyleClass().add(STYLE_CLASS);
    }

    private Node buildContent(Consumer<SearchData> onSearch) {
        var form = new FormRenderer(this.form);
        addAutoCompletionToLangField(form);
        return new VBox(form, buildButton(onSearch));
    }

    private Button buildButton(Consumer<SearchData> onSearch) {
        Button button = new Button(I18N.getGoogleBooksImportValue("google.books.add.form.search"));
        button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SEARCH));
        button.setDefaultButton(true);
        button.prefWidthProperty().bind(this.widthProperty());
        button.setOnAction(e -> onSearch.accept(searchData));
        VBox.setMargin(button, new Insets(0, 20, 10, 20));
        return button;
    }

    private void addAutoCompletionToLangField(FormRenderer src) {
        SimpleTextControl control = (SimpleTextControl) src.lookup(".languageSelector");
        TextField textField = (TextField) ( (StackPane) control.getChildren().get(1)).getChildren().get(0);
        textField.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                new LanguageSelection(context, locale -> textField.setText(locale.getLanguage())).show();
            }
        });
    }

    private Form buildForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(searchData.generalText)
                                .placeholder("google.books.add.form.gtext.prompt")
                                .label("google.books.add.form.gtext"),
                        Field.ofStringType(searchData.author)
                                .placeholder("google.books.add.form.author.prompt")
                                .label("google.books.add.form.author")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(searchData.title)
                                .placeholder("google.books.add.form.title.prompt")
                                .label("google.books.add.form.title")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(searchData.publisher)
                                .placeholder("google.books.add.form.publisher.prompt")
                                .label("google.books.add.form.publisher")
                                .span(ColSpan.THIRD),
                        Field.ofStringType(searchData.isbn)
                                .placeholder("google.books.add.form.isbn.prompt")
                                .label("google.books.add.form.isbn")
                                .span(ColSpan.TWO_THIRD),
                        Field.ofStringType(searchData.language)
                                .styleClass("languageSelector")
                                .placeholder("google.books.add.form.lang.prompt")
                                .label("google.books.add.form.lang")
                                .span(ColSpan.THIRD),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(searchData.selectableFilters), searchData.filter)
                                .label("google.books.add.form.filter")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(searchData.selectableSorts), searchData.sort)
                                .label("google.books.add.form.sort")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofIntegerType(searchData.maxResults)
                                .label("google.books.add.form.maxresults")
                                .validate(IntegerRangeValidator.between(1, 40, "google.books.add.form.maxresults.incorrect"))
                )
        ).title("google.books.add.form.ftitle").binding(BindingMode.CONTINUOUS)
                .i18n(new ResourceBundleService(I18N.getGoogleBooksImportValues()));
    }

    public void clear() {
        this.searchData.clear();
    }

    final static class SearchData {
        private final BooleanProperty valid;

        private SearchData(@NotNull BooleanProperty validProperty) {
            this.valid = validProperty;
        }

        private final StringProperty generalText = new SimpleStringProperty("");
        private final StringProperty author = new SimpleStringProperty("");
        private final StringProperty title = new SimpleStringProperty("");
        private final StringProperty publisher = new SimpleStringProperty("");
        private final StringProperty isbn = new SimpleStringProperty("");
        private final StringProperty language = new SimpleStringProperty("");
        private final IntegerProperty maxResults = new SimpleIntegerProperty(10);

        private final ObservableList<Filter> selectableFilters = FXCollections.observableArrayList(
                new Filter(GoogleBooksQueryBuilder.PrintType.ALL, I18N.getGoogleBooksImportValues(), "google.books.add.form.filter.all"),
                new Filter(GoogleBooksQueryBuilder.PrintType.BOOKS, I18N.getGoogleBooksImportValues(), "google.books.add.form.filter.books"),
                new Filter(GoogleBooksQueryBuilder.PrintType.MAGAZINES, I18N.getGoogleBooksImportValues(), "google.books.add.form.filter.magazines")
        );
        private final ObjectProperty<Filter> filter = new SimpleObjectProperty<>(selectableFilters.get(0));

        private final ObservableList<SortType> selectableSorts = FXCollections.observableArrayList(
                new SortType(GoogleBooksQueryBuilder.SortType.REVELANCE, I18N.getGoogleBooksImportValues(), "google.books.add.form.sort.relevance"),
                new SortType(GoogleBooksQueryBuilder.SortType.NEWEST, I18N.getGoogleBooksImportValues(), "google.books.add.form.sort.newest")
        );
        private final ObjectProperty<SortType> sort = new SimpleObjectProperty<>(selectableSorts.get(0));

        public boolean isValid() {
            return valid.get();
        }

        public BooleanProperty validProperty() {
            return valid;
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

        public int getMaxResults() {
            return maxResults.get();
        }

        public IntegerProperty maxResultsProperty() {
            return maxResults;
        }

        public ObservableList<Filter> getSelectableFilters() {
            return selectableFilters;
        }

        public Filter getFilter() {
            return filter.get();
        }

        public ObjectProperty<Filter> filterProperty() {
            return filter;
        }

        public ObservableList<SortType> getSelectableSorts() {
            return selectableSorts;
        }

        public SortType getSort() {
            return sort.get();
        }

        public ObjectProperty<SortType> sortProperty() {
            return sort;
        }

        private void clear() {
            generalText.set(StringUtils.EMPTY);
            author.set(StringUtils.EMPTY);
            isbn.set(StringUtils.EMPTY);
            title.set(StringUtils.EMPTY);
            publisher.set(StringUtils.EMPTY);
            language.set(StringUtils.EMPTY);
        }

        public BluePrint asBluePrint() {
            return new BluePrint(
                    generalText.get(),
                    author.get(),
                    title.get(),
                    publisher.get(),
                    isbn.get(),
                    language.get(),
                    maxResults.get(),
                    filter.get(),
                    sort.get());
        }

        static class BluePrint {
            private final String generalText;
            private final String author;
            private final String title;
            private final String publisher;
            private final String isbn;
            private final String language;
            private final Integer maxResults;
            private final Filter filter;
            private final SortType sort;

            public BluePrint(String generalText,
                             String author,
                             String title,
                             String publisher,
                             String isbn,
                             String language,
                             Integer maxResults,
                             Filter filter,
                             SortType sort) {
                this.generalText = generalText;
                this.author = author;
                this.title = title;
                this.publisher = publisher;
                this.isbn = isbn;
                this.language = language;
                this.maxResults = maxResults;
                this.filter = filter;
                this.sort = sort;
            }

            public String getGeneralText() {
                return generalText;
            }

            public String getAuthor() {
                return author;
            }

            public String getTitle() {
                return title;
            }

            public String getPublisher() {
                return publisher;
            }

            public String getIsbn() {
                return isbn;
            }

            public String getLanguage() {
                return language;
            }

            public Integer getMaxResults() {
                return maxResults;
            }

            public Filter getFilter() {
                return filter;
            }

            public SortType getSort() {
                return sort;
            }
        }
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
