package com.dansoftware.libraryapp.gui.googlebooks;

import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.util.BaseFXUtils;
import com.dansoftware.libraryapp.gui.util.ImagePlaceHolder;
import com.dansoftware.libraryapp.gui.util.ReadOnlyRating;
import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link GoogleBooksTable} is a table-view that used for showing Google Books.
 * <p>
 * It's columns are represented by the {@link ColumnType} enum.
 *
 * @author Daniel Gyorffy
 */
public class GoogleBooksTable extends TableView<Volume> {

    public enum ColumnType {
        INDEX_COLUMN("google.books.table.column.index", IndexColumn.class, true, table -> new IndexColumn(table.startIndex)),
        TYPE_INDICATOR_COLUMN("google.books.table.column.typeindicator", TypeIndicatorColumn.class, true, table -> new TypeIndicatorColumn()),
        THUMBNAIL_COLUMN("google.books.table.column.thumbnail", ThumbnailColumn.class, true, table -> new ThumbnailColumn()),
        ISBN_COLUMN("google.books.table.column.isbn", ISBNColumn.class, false, table -> new ISBNColumn()),
        ISBN_10_COLUMN("google.books.table.column.isbn10", ISBN10Column.class, false, table -> new ISBN10Column()),
        ISBN_13_COLUMN("google.books.table.column.isbn13", ISBN13Column.class, true, table -> new ISBN13Column()),
        AUTHOR_COLUMN("google.books.table.column.author", AuthorColumn.class, true, table -> new AuthorColumn()),
        TITLE_COLUMN("google.books.table.column.title", TitleColumn.class, true, table -> new TitleColumn()),
        SUB_TITLE_COLUMN("google.books.table.column.subtitle", SubtitleColumn.class, false, table -> new SubtitleColumn()),
        PUBLISHER_COLUMN("google.books.table.column.publisher", PublisherColumn.class, true, table -> new PublisherColumn()),
        LANG_COLUMN("google.books.table.column.lang", LangColumn.class, true, table -> new LangColumn()),
        DATE_COLUMN("google.books.table.column.date", DateColumn.class, true, table -> new DateColumn()),
        RANK_COLUMN("google.books.table.column.rank", RankColumn.class, true, table -> new RankColumn()),
        BROWSER_COLUMN("google.books.table.column.browse", BrowserColumn.class, false, table -> new BrowserColumn());

        private final String i18n;
        private final Class<? extends TableColumn<Volume, String>> tableColumnClass;
        private final boolean defaultVisible;
        private final Function<GoogleBooksTable, ? extends TableColumn<Volume, String>> createPolicy;

        <T extends TableColumn<Volume, String>> ColumnType(String i18n,
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
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder());
    }

    public void buildDefaultColumns() {
        this.getColumns().clear();
        Arrays.stream(ColumnType.values())
                .filter(ColumnType::isDefaultVisible)
                .forEach(this::addColumn);
    }

    public List<ColumnType> getShowingColumns() {
        return getColumns().stream()
                .map(col -> (Column) col)
                .map(col -> col.columnType)
                .collect(Collectors.toList());
    }

    public boolean isColumnShown(@Nullable ColumnType columnType) {
        return getColumns().stream()
                .map(col -> (Column) col)
                .map(col -> col.columnType)
                .anyMatch(col -> col.equals(columnType));
    }

    public void removeAllColumns() {
        this.getColumns().clear();
    }

    public void removeColumn(@NotNull ColumnType columnType) {
        this.getColumns().removeIf(col -> ((Column) col).columnType.equals(columnType));
    }

    public void addColumn(@NotNull ColumnType columnType) {
        addColumn(columnType.tableColumnClass, columnType.createPolicy);
    }

    private void addColumn(Class<? extends TableColumn<Volume, String>> tableColumnClass,
                           Function<GoogleBooksTable, ? extends TableColumn<Volume, String>> createAction) {
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

    private static class Column extends TableColumn<Volume, String> {
        private final ColumnType columnType;

        public Column(@NotNull ColumnType columnType, boolean i18n) {
            this.columnType = Objects.requireNonNull(columnType);
            this.setReorderable(false);
            if (i18n) setText(I18N.getGoogleBooksImportValue(columnType.getI18Nkey()));
        }

        public Column(@NotNull ColumnType columnType) {
            this(columnType, true);
        }

        protected Volume.VolumeInfo getVolumeInfo(TableCell<Volume, String> tableCell) {
            try {
                return getTableView().getItems().get(tableCell.getIndex()).getVolumeInfo();
            } catch (java.lang.IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    private static abstract class SimpleVolumeInfoColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        public SimpleVolumeInfoColumn(@NotNull ColumnType columnType) {
            this(columnType, true);
        }

        public SimpleVolumeInfoColumn(@NotNull ColumnType columnType, boolean i18n) {
            super(columnType, i18n);
            setCellFactory(this);
        }

        protected abstract Object getValue(Volume.VolumeInfo volumeInfo);

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
                        Object value = getValue(getVolumeInfo(this));
                        setText(value == null ? " - " : value.toString());
                    }
                }
            };
        }
    }


    private static final class IndexColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(ColumnType.INDEX_COLUMN, false);
            this.startIndexProperty = startIndexProperty;
            setCellFactory(this);
            setMinWidth(COLUMN_WIDTH_UNIT);
            setMaxWidth(COLUMN_WIDTH_UNIT);
        }

        @Override
        public TableCell<Volume, String> call(TableColumn<Volume, String> tableCol) {
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

    private static final class TypeIndicatorColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        TypeIndicatorColumn() {
            super(ColumnType.TYPE_INDICATOR_COLUMN, false);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        setGraphic(new MaterialDesignIconView(volume.isMagazine() ? MaterialDesignIcon.NEWSPAPER : MaterialDesignIcon.BOOK));
                        setTooltip(new Tooltip(I18N.getGoogleBooksImportValue(volume.isMagazine() ? "google.books.magazine" : "google.books.book")));
                    }
                }
            };
        }

    }

    private static final class ThumbnailColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        ThumbnailColumn() {
            super(ColumnType.THUMBNAIL_COLUMN);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        Optional.ofNullable(volume.getImageLinks())
                                .map(Volume.VolumeInfo.ImageLinks::getThumbnail)
                                .ifPresentOrElse(thumbnail -> {
                                    setGraphic(new ImagePlaceHolder(80) {{
                                        setHeight(PREF_HEIGHT);
                                    }});
                                    BaseFXUtils.loadImage(thumbnail, image -> {
                                        if (volume.equals(getVolumeInfo(this))) {
                                            setGraphic(new ImageView(image));
                                        }
                                    });
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

    private static final class ISBN10Column extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        ISBN10Column() {
            super(ColumnType.ISBN_10_COLUMN);
            setCellFactory(this);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        Optional.ofNullable(volume.getIndustryIdentifiers())
                                .ifPresentOrElse(industryIdentifiers ->
                                                industryIdentifiers.stream()
                                                        .filter(identifier ->
                                                                Volume
                                                                        .VolumeInfo
                                                                        .IndustryIdentifier
                                                                        .ISBN_10
                                                                        .equals(identifier.getType()))
                                                        .findAny()
                                                        .ifPresentOrElse(identifier -> setText(identifier.getIdentifier()), () -> setText(" - ")),
                                        () -> setText(" - "));
                    }
                }
            };
        }
    }

    private static final class ISBN13Column extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        ISBN13Column() {
            super(ColumnType.ISBN_13_COLUMN);
            setCellFactory(this);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        Optional.ofNullable(volume.getIndustryIdentifiers())
                                .ifPresentOrElse(industryIdentifiers -> industryIdentifiers.stream().filter(identifier ->
                                                identifier.getType()
                                                        .equals(Volume.VolumeInfo.IndustryIdentifier.ISBN_13))
                                                .findAny()
                                                .ifPresentOrElse(identifier -> setText(identifier.getIdentifier()),
                                                        () -> setText(" - ")),
                                        () -> setText(" - "));
                    }
                }
            };
        }
    }

    private static final class ISBNColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        ISBNColumn() {
            super(ColumnType.ISBN_COLUMN);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        setText(StringUtils.getIfBlank(volume.getIndustryIdentifiersAsString(), () -> "-"));
                    }
                }
            };
        }
    }

    private static final class AuthorColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        AuthorColumn() {
            super(ColumnType.AUTHOR_COLUMN);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        Optional.ofNullable(volume.getAuthors())
                                .ifPresentOrElse(authors -> setText(String.join(", ", authors)),
                                        () -> setText("-"));
                    }
                }
            };
        }
    }

    private static final class TitleColumn extends SimpleVolumeInfoColumn {
        TitleColumn() {
            super(ColumnType.TITLE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getTitle();
        }
    }

    private static final class SubtitleColumn extends SimpleVolumeInfoColumn {
        SubtitleColumn() {
            super(ColumnType.SUB_TITLE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getSubtitle();
        }
    }

    private static final class PublisherColumn extends SimpleVolumeInfoColumn {
        PublisherColumn() {
            super(ColumnType.PUBLISHER_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getPublisher();
        }
    }

    private static final class LangColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        LangColumn() {
            super(ColumnType.LANG_COLUMN);
            setCellFactory(this);
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
                        Volume.VolumeInfo volumeInfo = getVolumeInfo(this);
                        Optional.ofNullable(volumeInfo.getLanguage())
                                .map(Locale::forLanguageTag)
                                .map(Locale::getDisplayLanguage)
                                .ifPresentOrElse(this::setText, () -> setText(null));
                    }
                }
            };
        }
    }

    private static final class DateColumn extends SimpleVolumeInfoColumn {

        DateColumn() {
            super(ColumnType.DATE_COLUMN);
        }

        @Override
        protected Object getValue(Volume.VolumeInfo volumeInfo) {
            return volumeInfo.getPublishedDate();
        }
    }

    private static final class RankColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        RankColumn() {
            super(ColumnType.RANK_COLUMN);
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
                        Volume.VolumeInfo volumeInfo = getVolumeInfo(this);
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

    private static final class BrowserColumn extends Column
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {
        BrowserColumn() {
            super(ColumnType.BROWSER_COLUMN, false);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        Optional.ofNullable(volume.getPreviewLink())
                                .ifPresent(link -> setGraphic(new WebsiteHyperLink(I18N.getGoogleBooksImportValue("google.books.table.browser.open"), link)));
                    }
                }
            };
        }
    }
}
