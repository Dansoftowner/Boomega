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

import com.dansoftware.boomega.gui.control.*;
import com.dansoftware.boomega.service.googlebooks.Volume;
import com.dansoftware.boomega.gui.util.BaseFXUtils;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.dansoftware.boomega.gui.control.BaseTable.ColumnType.*;

/**
 * A {@link GoogleBooksTable} is a table-view that used for showing Google Books.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBooksTable extends BaseTable<Volume> {

    public static final ColumnType INDEX_COLUMN =
            new ColumnType(
                    "index",
                    "google.books.table.column.index",
                    GoogleBooksTable.class,
                    table -> new IndexColumn(table.startIndex),
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType TYPE_INDICATOR_COLUMN =
            new ColumnType(
                    "type_indicator",
                    "google.books.table.column.typeindicator",
                    TypeIndicatorColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED
            );

    public static final ColumnType THUMBNAIL_COLUMN =
            new ColumnType(
                    "thumbnail",
                    "google.books.table.column.thumbnail",
                    ThumbnailColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType ISBN_COLUMN =
            new ColumnType(
                    "isbn",
                    "google.books.table.column.isbn",
                    ISBNColumn::new,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType ISBN_10_COLUMN =
            new ColumnType(
                    "isbn10",
                    "google.books.table.column.isbn10",
                    ISBN10Column::new,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType ISBN_13_COLUMN =
            new ColumnType(
                    "isbn13",
                    "google.books.table.column.isbn13",
                    ISBN13Column::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType AUTHOR_COLUMN =
            new ColumnType(
                    "author",
                    "google.books.table.column.author",
                    AuthorColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType TITLE_COLUMN =
            new ColumnType(
                    "title",
                    "google.books.table.column.title",
                    TitleColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType SUB_TITLE_COLUMN =
            new ColumnType(
                    "subtitle",
                    "google.books.table.column.subtitle",
                    SubtitleColumn::new,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType PUBLISHER_COLUMN =
            new ColumnType(
                    "publisher",
                    "google.books.table.column.subtitle",
                    PublisherColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType LANG_COLUMN =
            new ColumnType(
                    "lang",
                    "google.books.table.column.lang",
                    LangColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType DATE_COLUMN =
            new ColumnType(
                    "date",
                    "google.books.table.column.date",
                    DateColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType RANK_COLUMN =
            new ColumnType(
                    "rank",
                    "google.books.table.column.rank",
                    RankColumn::new,
                    DEFAULT_VISIBLE,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType BROWSER_COLUMN =
            new ColumnType(
                    "browse",
                    "google.books.table.column.browse",
                    BrowserColumn::new,
                    INTERNATIONALIZED,
                    TEXT_GUI_VISIBLE
            );

    private static final List<ColumnType> columnsList =
            List.of(
                    INDEX_COLUMN,
                    TYPE_INDICATOR_COLUMN,
                    THUMBNAIL_COLUMN,
                    ISBN_COLUMN,
                    ISBN_10_COLUMN,
                    ISBN_13_COLUMN,
                    AUTHOR_COLUMN,
                    TITLE_COLUMN,
                    SUB_TITLE_COLUMN,
                    PUBLISHER_COLUMN,
                    LANG_COLUMN,
                    DATE_COLUMN,
                    RANK_COLUMN,
                    BROWSER_COLUMN
            );

    public static List<ColumnType> columns() {
        return columnsList;
    }

    public static Optional<ColumnType> columnById(@NotNull String id) {
        return columns().stream().filter(it -> it.getId().equals(id)).findAny();
    }

    private static final String STYLE_CLASS = "google-books-table";

    private final IntegerProperty startIndex;

    GoogleBooksTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(buildPlaceHolder());
    }

    private Node buildPlaceHolder() {
        return new TableViewPlaceHolder(
                this,
                () -> I18N.getValue("google.books.table.place.holder"),
                () -> I18N.getValue("google.books.table.place.holder.no.col")
        );
    }

    public void buildDefaultColumns() {
        this.getColumns().clear();
        columns().stream()
                .filter(ColumnType::isDefaultVisible)
                .forEach(this::addColumnType);
    }

    public int getStartIndex() {
        return startIndex.get();
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex.set(startIndex);
    }

    private static abstract class SimpleVolumeInfoColumn extends SortableColumn<Volume>
            implements Callback<TableColumn.CellDataFeatures<Volume, String>, ObservableValue<String>> {

        public SimpleVolumeInfoColumn(@NotNull ColumnType columnType) {
            super(columnType);
            setCellValueFactory(this);
        }

        protected abstract Object getValue(Volume.VolumeInfo volumeInfo);

        @Override
        public ObservableValue<String> call(CellDataFeatures<Volume, String> cellData) {
            return BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(getValue(cellData.getValue().getVolumeInfo()))
                            .map(Object::toString)
                            .orElse("-"));

        }
    }

    private static final class IndexColumn extends Column<Volume, Integer> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(INDEX_COLUMN);
            setSortable(false);
            setMinWidth(COLUMN_WIDTH_UNIT);
            setMaxWidth(COLUMN_WIDTH_UNIT);
            setCellValueFactory(cellData -> new ObservableValueBase<>() {
                @Override
                public Integer getValue() {
                    return 1 + startIndexProperty.get() + cellData.getTableView()
                            .getItems()
                            .indexOf(cellData.getValue());
                }
            });
        }
    }

    private static final class TypeIndicatorColumn extends Column<Volume, String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        TypeIndicatorColumn() {
            super(TYPE_INDICATOR_COLUMN);
            setSortable(false);
            setCellFactory(this);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Volume, String> call(TableColumn<Volume, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {
                        Volume.VolumeInfo volume = this.getTableView().getItems().get(getIndex()).getVolumeInfo();
                        setGraphic(new MaterialDesignIconView(volume.isMagazine() ? MaterialDesignIcon.NEWSPAPER : MaterialDesignIcon.BOOK));
                        setTooltip(new Tooltip(I18N.getValue(volume.isMagazine() ? "google.books.magazine" : "google.books.book")));
                    }
                }
            };
        }

    }

    private static final class ThumbnailColumn extends Column<Volume, String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        ThumbnailColumn() {
            super(THUMBNAIL_COLUMN);
            setSortable(false);
            setCellFactory(this);
            setMinWidth(20);
            setPrefWidth(200);
        }

        @Override
        public TableCell<Volume, String> call(TableColumn<Volume, String> param) {
            return new TableCell<>() {
                private static final int PREF_HEIGHT = 184;

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                        setPrefHeight(USE_COMPUTED_SIZE);
                    } else {
                        setPrefHeight(PREF_HEIGHT);
                        Volume.VolumeInfo volume = this.getTableView().getItems().get(getIndex()).getVolumeInfo();
                        Optional.ofNullable(volume.getImageLinks())
                                .map(Volume.VolumeInfo.ImageLinks::getThumbnail)
                                .ifPresentOrElse(thumbnail -> {
                                    setGraphic(new ImagePlaceHolder(80) {{
                                        setHeight(PREF_HEIGHT);
                                    }});
                                    BaseFXUtils.loadImage(thumbnail, image -> {
                                        if (volume.equals(getCurrentVolumeInfo())) {
                                            setGraphic(new ImageView(image));
                                        }
                                    });
                                    setText(null);
                                }, () -> {
                                    setGraphic(null);
                                    setText(I18N.getValue("google.books.table.thumbnail.not.available"));
                                });
                    }
                }

                private Volume.VolumeInfo getCurrentVolumeInfo() {
                    try {
                        return this.getTableView().getItems().get(getIndex()).getVolumeInfo();
                    } catch (IndexOutOfBoundsException e) {
                        return null;
                    }

                }
            };
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class ISBN10Column extends Column<Volume, String> {

        ISBN10Column() {
            super(ISBN_10_COLUMN);
            setSortable(false);
            setCellValueFactory(cellData -> BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(cellData.getValue())
                            .map(Volume::getVolumeInfo)
                            .map(Volume.VolumeInfo::getIndustryIdentifiers)
                            .flatMap(identifiers -> identifiers.stream()
                                    .filter(Volume.VolumeInfo.IndustryIdentifier::isIsbn10)
                                    .map(Volume.VolumeInfo.IndustryIdentifier::getIdentifier)
                                    .findAny()).orElse("-"))
            );
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private static final class ISBN13Column extends Column<Volume, String>
            implements Callback<TableColumn.CellDataFeatures<Volume, String>, ObservableValue<String>> {
        ISBN13Column() {
            super(ISBN_13_COLUMN);
            setSortable(false);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Volume, String> cellData) {
            return BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(cellData.getValue())
                            .map(Volume::getVolumeInfo)
                            .map(Volume.VolumeInfo::getIndustryIdentifiers)
                            .flatMap(identifiers -> identifiers.stream()
                                    .filter(Volume.VolumeInfo.IndustryIdentifier::isIsbn13)
                                    .map(Volume.VolumeInfo.IndustryIdentifier::getIdentifier)
                                    .findAny()).orElse("-"));
        }
    }

    private static final class ISBNColumn extends Column<Volume, String>
            implements Callback<TableColumn.CellDataFeatures<Volume, String>, ObservableValue<String>> {
        ISBNColumn() {
            super(ISBN_COLUMN);
            setSortable(false);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Volume, String> cellData) {
            return BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(cellData.getValue().getVolumeInfo())
                            .map(Volume.VolumeInfo::getIndustryIdentifiersAsString)
                            .orElse("-"));
        }
    }

    private static final class AuthorColumn extends SortableColumn<Volume> {
        AuthorColumn() {
            super(AUTHOR_COLUMN);
            setCellValueFactory(cellData ->
                    BaseFXUtils.constantObservable(() ->
                            Optional.ofNullable(cellData.getValue().getVolumeInfo())
                                    .map(Volume.VolumeInfo::getAuthors)
                                    .map(authors -> String.join(", ", authors))
                                    .orElse("-"))
            );
        }
    }

    private static final class TitleColumn extends SimpleVolumeInfoColumn {
        TitleColumn() {
            super(TITLE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getTitle();
        }
    }

    private static final class SubtitleColumn extends SimpleVolumeInfoColumn {
        SubtitleColumn() {
            super(SUB_TITLE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getSubtitle();
        }
    }

    private static final class PublisherColumn extends SimpleVolumeInfoColumn {
        PublisherColumn() {
            super(PUBLISHER_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getPublisher();
        }
    }

    private static final class LangColumn extends Column<Volume, String>
            implements Callback<TableColumn.CellDataFeatures<Volume, String>, ObservableValue<String>> {
        LangColumn() {
            super(LANG_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Volume, String> cellData) {
            return BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(cellData.getValue().getVolumeInfo())
                            .map(Volume.VolumeInfo::getLanguage)
                            .map(Locale::forLanguageTag)
                            .map(Locale::getDisplayLanguage)
                            .orElse("-"));
        }
    }

    private static final class DateColumn extends SimpleVolumeInfoColumn {

        DateColumn() {
            super(DATE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getPublishedDate();
        }
    }

    private static final class RankColumn extends Column<Volume, String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        RankColumn() {
            super(RANK_COLUMN);
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume, String> call(TableColumn<Volume, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volumeInfo = this.getTableView().getItems().get(getIndex()).getVolumeInfo();
                        Optional.ofNullable(volumeInfo.getAverageRating())
                                .ifPresentOrElse(rating -> {
                                    setGraphic(buildGraphic(rating.intValue(), volumeInfo.getRatingsCount()));
                                    setText(null);
                                }, () -> {
                                    setGraphic(null);
                                    setText("-");
                                });
                    }
                }

                private Node buildGraphic(int rating, int ratingsCount) {
                    Rating ratingGraphic = new ReadOnlyRating(5, rating);
                    return new Group(new VBox(3, ratingGraphic, new StackPane(new Label("(" + ratingsCount + ")"))));
                }
            };
        }
    }

    private static final class BrowserColumn extends Column<Volume, String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        BrowserColumn() {
            super(BROWSER_COLUMN);
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setPrefWidth(200);
        }

        @Override
        public TableCell<Volume, String> call(TableColumn<Volume, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Volume.VolumeInfo volume = this.getTableView().getItems().get(getIndex()).getVolumeInfo();
                        Optional.ofNullable(volume.getPreviewLink())
                                .ifPresent(link -> setGraphic(new WebsiteHyperLink(I18N.getValue("google.books.table.browser.open"), link)));
                    }
                }
            };
        }
    }
}
