package com.dansoftware.libraryapp.gui.mainview.module.googlebooks;

import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.util.BaseFXUtils;
import com.dansoftware.libraryapp.gui.util.ImagePlaceHolder;
import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class GoogleBooksTable extends TableView<Volume.VolumeInfo> {

    public enum ColumnType {
        INDEX_COLUMN( "", IndexColumn.class, true, table -> new IndexColumn(table.startIndex)),
        TYPE_INDICATOR_COLUMN( "", TypeIndicatorColumn.class, true, table -> new TypeIndicatorColumn()),
        THUMBNAIL_COLUMN( "google.books.table.column.thumbnail", ThumbnailColumn.class, true, table -> new ThumbnailColumn()),
        ISBN_COLUMN( "google.books.table.column.isbn", ISBNColumn.class, true, table -> new ISBNColumn()),
        AUTHOR_COLUMN("google.books.table.column.author", AuthorColumn.class, true, table -> new AuthorColumn()),
        TITLE_COLUMN( "google.books.table.column.title", TitleColumn.class, true, table -> new TitleColumn()),
        PUBLISHER_COLUMN("google.books.table.column.publisher", PublisherColumn.class, true, table -> new PublisherColumn()),
        LANG_COLUMN( "google.books.table.column.lang", LangColumn.class, true, table -> new LangColumn()),
        DATE_COLUMN( "google.books.table.column.date", DateColumn.class, true, table -> new DateColumn()),
        RANK_COLUMN( "google.books.table.column.rank", RankColumn.class, true, table -> new RankColumn()),
        BROWSER_COLUMN( "", BrowserColumn.class,false, table -> new BrowserColumn());

        private final String i18n;
        private final Class<? extends TableColumn<Volume.VolumeInfo, String>> tableColumnClass;
        private final boolean defaultVisible;
        private final Function<GoogleBooksTable, ? extends TableColumn<Volume.VolumeInfo, String>> createPolicy;

        <T extends TableColumn<Volume.VolumeInfo, String>> ColumnType(String i18n,
                                                                      Class<T> tableColumnClass,
                                                                      boolean defaultVisible,
                                                                      Function<GoogleBooksTable, T> createPolicy) {
            this.i18n = i18n;
            this.tableColumnClass = tableColumnClass;
            this.defaultVisible = defaultVisible;
            this.createPolicy = createPolicy;
        }

        public boolean isDefaultVisible() {
            return defaultVisible;
        }

        public String getI18Nkey() {
            return i18n;
        }
    }

    private static final String STYLE_CLASS = "google-books-table";

    private final IntegerProperty startIndex;

    GoogleBooksTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.init();
    }

    private void init() {
        getStyleClass().add(STYLE_CLASS);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getColumns().addAll(
                new IndexColumn(startIndex),
                new TypeIndicatorColumn(),
                new ThumbnailColumn(),
                new ISBNColumn(),
                new AuthorColumn(),
                new TitleColumn(),
                new PublisherColumn(),
                new LangColumn(),
                new DateColumn(),
                new RankColumn(),
                new BrowserColumn()
        );
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder());
    }

    public void createColumn(@NotNull ColumnType columnType) {
        createColumn(columnType.tableColumnClass, columnType.createPolicy);
    }

    private void createColumn(Class<? extends TableColumn<Volume.VolumeInfo, String>> tableColumnClass,
                              Function<GoogleBooksTable, ? extends TableColumn<Volume.VolumeInfo, String>> createAction) {
            this.getColumns().add(createAction.apply(this));
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

    private static final class PlaceHolder extends StackPane {
        PlaceHolder() {
            getChildren().add(new Group(new VBox(
                    //TODO: ICON
                    new Label(I18N.getGoogleBooksImportValue("google.books.table.place.holder"))
            )));
        }
    }

    public static class Column extends TableColumn<Volume.VolumeInfo, String> {
        private final ColumnType columnType;

        public Column(@NotNull ColumnType columnType) {
            this.columnType = Objects.requireNonNull(columnType);
        }

        public Column(@NotNull ColumnType columnType, @NotNull String i18n) {
            super(I18N.getGoogleBooksImportValue(i18n));
            this.columnType = Objects.requireNonNull(columnType);
        }
    }

    public static final class IndexColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(ColumnType.INDEX_COLUMN);
            this.startIndexProperty = startIndexProperty;
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(COLUMN_WIDTH_UNIT);
            setMaxWidth(COLUMN_WIDTH_UNIT);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> tableCol) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(Integer.toString(startIndexProperty.get() + getIndex() + 1));
                        int preferredColumnWidth = getText().length() * COLUMN_WIDTH_UNIT;
                        if (tableCol.getWidth() < preferredColumnWidth) {
                            tableCol.setMinWidth(preferredColumnWidth);
                            tableCol.setMaxWidth(preferredColumnWidth);
                        }
                    }
                }
            };
        }
    }

    public static final class TypeIndicatorColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        TypeIndicatorColumn() {
            super(ColumnType.TYPE_INDICATOR_COLUMN);
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        setGraphic(new MaterialDesignIconView(volume.isMagazine() ? MaterialDesignIcon.NEWSPAPER : MaterialDesignIcon.BOOK));
                    }
                }
            };
        }

    }

    public static final class ThumbnailColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        ThumbnailColumn() {
            super(ColumnType.THUMBNAIL_COLUMN, "google.books.table.column.thumbnail");
            setCellFactory(this);
            setMinWidth(20);
            setPrefWidth(200);
            setReorderable(false);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
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
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getImageLinks())
                                .map(Volume.VolumeInfo.ImageLinks::getThumbnail)
                                .ifPresentOrElse(thumbnail -> {
                                    setGraphic(new ImagePlaceHolder(80) {{ setHeight(PREF_HEIGHT); }});
                                    BaseFXUtils.loadImage(thumbnail, image -> setGraphic(new ImageView(image)));
                                    setText(null);
                                }, () -> {
                                    setGraphic(null);
                                    setText(I18N.getGoogleBooksImportValue("google.books.table.thumbnail.not.available"));
                                });
                    }
                }
            };
        }
    }

    public static final class ISBNColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        ISBNColumn() {
            super(ColumnType.ISBN_COLUMN,"google.books.table.column.isbn");
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getIndustryIdentifiers())
                                .map(identifiers -> identifiers.get(0))
                                .map(Volume.VolumeInfo.IndustryIdentifier::getIdentifier)
                                .ifPresentOrElse(this::setText, () -> setText("-"));
                    }
                }
            };
        }
    }

    public static final class AuthorColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        AuthorColumn() {
            super(ColumnType.AUTHOR_COLUMN, "google.books.table.column.author");
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getAuthors())
                                .ifPresentOrElse(authors -> setText(String.join(", ", authors)),
                                        () -> setText("-"));
                    }
                }
            };
        }
    }

    public static final class TitleColumn extends Column {
        TitleColumn() {
            super(ColumnType.TITLE_COLUMN, "google.books.table.column.title");
            setCellValueFactory(new PropertyValueFactory<>("title"));
        }
    }

    public static final class PublisherColumn extends Column {
        PublisherColumn() {
            super(ColumnType.PUBLISHER_COLUMN, "google.books.table.column.publisher");
            setCellValueFactory(new PropertyValueFactory<>("publisher"));
        }
    }

    public static final class LangColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        LangColumn() {
            super(ColumnType.LANG_COLUMN, "google.books.table.column.lang");
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Volume.VolumeInfo volumeInfo = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volumeInfo.getLanguage())
                                .map(Locale::forLanguageTag)
                                .map(Locale::getDisplayLanguage)
                                .ifPresentOrElse(this::setText, () -> setText(null));
                    }
                }
            };
        }
    }

    public static final class DateColumn extends Column {

        DateColumn() {
            super(ColumnType.DATE_COLUMN, "google.books.table.column.date");
            setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        }
    }

    public static final class RankColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {

        RankColumn() {
            super(ColumnType.RANK_COLUMN,"google.books.table.column.rank");
            setCellFactory(this);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Volume.VolumeInfo volumeInfo = getTableView().getItems().get(getIndex());
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
                    Rating ratingGraphic = new Rating(5, rating);
                    ratingGraphic.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
                    return new Group(new VBox(3, ratingGraphic, new StackPane(new Label("(" + ratingsCount + ")"))));
                }
            };
        }
    }

    public static final class BrowserColumn extends Column
            implements Callback<TableColumn<Volume.VolumeInfo, String>, TableCell<Volume.VolumeInfo, String>> {
        BrowserColumn() {
            super(ColumnType.BROWSER_COLUMN);
            setCellFactory(this);
            setReorderable(false);
            setMinWidth(50);
            setPrefWidth(200);
        }

        @Override
        public TableCell<Volume.VolumeInfo, String> call(TableColumn<Volume.VolumeInfo, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Volume.VolumeInfo volume = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(volume.getPreviewLink())
                                .ifPresent(link -> setGraphic(new WebsiteHyperLink(I18N.getGoogleBooksImportValue("google.books.table.browser.open"), link)));
                    }
                }
            };
        }
    }
}
