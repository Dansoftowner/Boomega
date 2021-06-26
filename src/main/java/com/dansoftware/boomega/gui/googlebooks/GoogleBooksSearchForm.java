/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.googlebooks;

import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.control.TextFieldLanguageSelectorControl;
import com.dansoftware.boomega.i18n.I18N;
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQueryBuilder;
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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;
import java.util.function.Consumer;

class GoogleBooksSearchForm extends TitledPane {

    private static final String STYLE_CLASS = "google-books-import-form";

    private final Context context;
    private final FormProperties formProperties;
    private final Form form;

    GoogleBooksSearchForm(@NotNull Context context, @NotNull Consumer<SearchParameters> onSearch) {
        super(I18N.getValue("google.books.add.form.section.title"), null);
        this.context = context;
        this.formProperties = new FormProperties();
        this.form = buildForm();
        this.setContent(buildContent(onSearch));
        this.getStyleClass().add(STYLE_CLASS);
    }

    private Node buildContent(Consumer<SearchParameters> onSearch) {
        var form = new FormRenderer(this.form);
        addAutoCompletionToLangField(form);
        return new VBox(form, buildButton(onSearch));
    }

    private Button buildButton(Consumer<SearchParameters> onSearch) {
        Button button = new Button(I18N.getValue("google.books.add.form.search"));
        button.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SEARCH));
        button.setDefaultButton(true);
        button.prefWidthProperty().bind(this.widthProperty());
        button.setOnAction(e -> onSearch.accept(formProperties.asSearchParams()));
        button.disableProperty().bind(formProperties.valid.not().or(form.validProperty().not()));
        VBox.setMargin(button, new Insets(0, 20, 10, 20));
        return button;
    }

    private void addAutoCompletionToLangField(FormRenderer src) {
        SimpleTextControl control = (SimpleTextControl) src.lookup(".languageSelector");
        TextField textField = (TextField) control.lookup(".text-field");
        TextFieldLanguageSelectorControl.applyOnTextField(context, textField);
    }

    private Form buildForm() {
        return Form.of(
                Group.of(
                        Field.ofStringType(formProperties.generalText)
                                .placeholder("google.books.add.form.gtext.prompt")
                                .label("google.books.add.form.gtext"),
                        Field.ofStringType(formProperties.author)
                                .placeholder("google.books.add.form.author.prompt")
                                .label("google.books.add.form.author")
                                .span(ColSpan.HALF),
                        Field.ofStringType(formProperties.title)
                                .placeholder("google.books.add.form.title.prompt")
                                .label("google.books.add.form.title")
                                .span(ColSpan.HALF),
                        Field.ofStringType(formProperties.publisher)
                                .placeholder("google.books.add.form.publisher.prompt")
                                .label("google.books.add.form.publisher")
                                .span(ColSpan.HALF),
                        Field.ofStringType(formProperties.subject)
                                .placeholder("google.books.add.form.subject.prompt")
                                .label("google.books.add.form.subject")
                                .span(ColSpan.HALF),
                        Field.ofStringType(formProperties.isbn)
                                .placeholder("google.books.add.form.isbn.prompt")
                                .label("google.books.add.form.isbn")
                                .span(ColSpan.HALF),
                        Field.ofStringType(formProperties.language)
                                .styleClass("languageSelector")
                                .placeholder("google.books.add.form.lang.prompt")
                                .label("google.books.add.form.lang")
                                .span(ColSpan.HALF),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(formProperties.selectableFilters), formProperties.filter)
                                .label("google.books.add.form.filter")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofSingleSelectionType(new SimpleListProperty<>(formProperties.selectableSorts), formProperties.sort)
                                .label("google.books.add.form.sort")
                                .span(ColSpan.HALF)
                                .render(new SimpleRadioButtonControl<>()),
                        Field.ofIntegerType(formProperties.maxResults)
                                .label("google.books.add.form.maxresults")
                                .validate(IntegerRangeValidator.between(1, 40, "google.books.add.form.maxresults.incorrect"))
                )
        ).title("google.books.add.form.ftitle").binding(BindingMode.CONTINUOUS)
                .i18n(new ResourceBundleService(I18N.getValues()));
    }

    public void clear() {
        this.formProperties.clear();
    }

    private final static class FormProperties {
        private final BooleanBinding valid;

        private FormProperties() {
            this.valid = generalText.isNotEmpty()
                    .or(author.isNotEmpty())
                    .or(title.isNotEmpty())
                    .or(publisher.isNotEmpty())
                    .or(subject.isNotEmpty())
                    .or(isbn.isNotEmpty());
        }

        private final StringProperty generalText = new SimpleStringProperty("");
        private final StringProperty author = new SimpleStringProperty("");
        private final StringProperty title = new SimpleStringProperty("");
        private final StringProperty publisher = new SimpleStringProperty("");
        private final StringProperty subject = new SimpleStringProperty("");
        private final StringProperty isbn = new SimpleStringProperty("");
        private final StringProperty language = new SimpleStringProperty("");
        private final IntegerProperty maxResults = new SimpleIntegerProperty(10);

        private final ObservableList<Filter> selectableFilters = FXCollections.observableArrayList(
                new Filter(GoogleBooksQueryBuilder.PrintType.ALL, I18N.getValues(), "google.books.add.form.filter.all"),
                new Filter(GoogleBooksQueryBuilder.PrintType.BOOKS, I18N.getValues(), "google.books.add.form.filter.books"),
                new Filter(GoogleBooksQueryBuilder.PrintType.MAGAZINES, I18N.getValues(), "google.books.add.form.filter.magazines")
        );
        private final ObjectProperty<Filter> filter = new SimpleObjectProperty<>(selectableFilters.get(0));

        private final ObservableList<SortType> selectableSorts = FXCollections.observableArrayList(
                new SortType(GoogleBooksQueryBuilder.SortType.RELEVANCE, I18N.getValues(), "google.books.add.form.sort.relevance"),
                new SortType(GoogleBooksQueryBuilder.SortType.NEWEST, I18N.getValues(), "google.books.add.form.sort.newest")
        );
        private final ObjectProperty<SortType> sort = new SimpleObjectProperty<>(selectableSorts.get(0));

        public String getSubject() {
            return subject.get();
        }

        public StringProperty subjectProperty() {
            return subject;
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

        public SearchParameters asSearchParams() {
            return new SearchParameters()
                    .inText(generalText.get())
                    .authors(author.get())
                    .title(title.get())
                    .publisher(publisher.get())
                    .subject(subject.get())
                    .isbn(isbn.get())
                    .language(language.get())
                    .maxResults(maxResults.get())
                    .printType(filter.get().getType())
                    .sortType(sort.get().getType());
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
