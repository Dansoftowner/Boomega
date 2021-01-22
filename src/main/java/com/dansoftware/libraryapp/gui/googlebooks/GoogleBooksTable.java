package com.dansoftware.libraryapp.gui.googlebooks;

import com.dansoftware.libraryapp.googlebooks.Volume;
import com.dansoftware.libraryapp.gui.util.BaseFXUtils;
import com.dansoftware.libraryapp.gui.util.ImagePlaceHolder;
import com.dansoftware.libraryapp.gui.util.ReadOnlyRating;
import com.dansoftware.libraryapp.gui.util.WebsiteHyperLink;
import com.dansoftware.libraryapp.locale.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
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
        private final Class<? extends Column<?>> tableColumnClass;
        private final boolean defaultVisible;
        private final Function<GoogleBooksTable, ? extends Column<?>> createPolicy;

        <T extends Column<?>> ColumnType(String i18n,
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
    private final ObjectProperty<Consumer<Volume>> onItemDoubleClicked;
    private final ObjectProperty<Consumer<Volume>> onItemSecondaryDoubleClicked;

    GoogleBooksTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.onItemDoubleClicked = new SimpleObjectProperty<>();
        this.onItemSecondaryDoubleClicked = new SimpleObjectProperty<>();
        this.init();
    }

    private void init() {
        getStyleClass().add(STYLE_CLASS);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder(this));
        this.buildClickHandlingPolicy();
    }

    private void buildClickHandlingPolicy() {
        this.setRowFactory(table -> {
            TableRow<Volume> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (!tableRow.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    if (onItemDoubleClicked.get() != null) {
                        onItemDoubleClicked.get().accept(tableRow.getItem());
                    }
                } else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 2) {
                    if (onItemSecondaryDoubleClicked.get() != null) {
                        onItemSecondaryDoubleClicked.get().accept(tableRow.getItem());
                    }
                }
            });
            return tableRow;
        });
    }

    public void setSortingComparator(@NotNull Comparator<String> comparator) {
        this.getColumns().stream()
                .filter(col -> col instanceof AbcSortableColumn)
                .map(col -> (AbcSortableColumn) col)
                .forEach(col -> col.setComparator(comparator));
    }

    public void buildDefaultColumns() {
        this.getColumns().clear();
        Arrays.stream(ColumnType.values())
                .filter(ColumnType::isDefaultVisible)
                .forEach(this::addColumn);
    }

    public List<ColumnType> getShowingColumns() {
        return getColumns().stream()
                .map(col -> (Column<?>) col)
                .map(col -> col.columnType)
                .collect(Collectors.toList());
    }

    public boolean isColumnShown(@Nullable ColumnType columnType) {
        return getColumns().stream()
                .map(col -> (Column<?>) col)
                .map(col -> col.columnType)
                .anyMatch(col -> col.equals(columnType));
    }

    public void setOnItemDoubleClicked(Consumer<Volume> onItemDoubleClicked) {
        this.onItemDoubleClicked.set(onItemDoubleClicked);
    }

    public void setOnItemSecondaryDoubleClicked(Consumer<Volume> onItemSecondaryDoubleClicked) {
        this.onItemSecondaryDoubleClicked.set(onItemSecondaryDoubleClicked);
    }

    public void removeAllColumns() {
        this.getColumns().clear();
    }

    public void removeColumn(@NotNull ColumnType columnType) {
        this.getColumns().removeIf(col -> ((Column<?>) col).columnType.equals(columnType));
    }

    public void addColumns(ColumnType... columnTypes) {
        for (ColumnType columnType : columnTypes) {
            addColumn(columnType);
        }
    }

    public void addColumn(@NotNull ColumnType columnType) {
        addColumn(columnType.tableColumnClass, columnType.createPolicy);
    }

    private void addColumn(Class<? extends Column<?>> tableColumnClass,
                           Function<GoogleBooksTable, ? extends Column<?>> createAction) {
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
        private final BooleanBinding noColumns;

        PlaceHolder(@NotNull TableView<?> tableView) {
            this.noColumns = Bindings.isEmpty(tableView.getColumns());
            getChildren().add(new Group(new VBox(
                    //TODO: ICON
                    new Label() {{
                        noColumns.addListener((observable, oldValue, noColumns) -> {
                            if (noColumns)
                                this.setText(I18N.getGoogleBooksImportValue("google.books.table.place.holder.no.col"));
                            else
                                this.setText(I18N.getGoogleBooksImportValue("google.books.table.place.holder"));
                        });
                    }}
            )));
        }
    }

    private static class Column<T> extends TableColumn<Volume, T> {
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

    private static abstract class AbcSortableColumn extends Column<String> {
        AbcSortableColumn(@NotNull ColumnType columnType) {
            this(columnType, true);
        }

        AbcSortableColumn(@NotNull ColumnType columnType, boolean i18n) {
            super(columnType, i18n);
            setSortable(true);
        }
    }

    private static abstract class SimpleVolumeInfoColumn extends AbcSortableColumn
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        public SimpleVolumeInfoColumn(@NotNull ColumnType columnType) {
            this(columnType, true);
        }

        public SimpleVolumeInfoColumn(@NotNull ColumnType columnType, boolean i18n) {
            super(columnType, i18n);
            setCellValueFactory(cellData ->
                    BaseFXUtils.constantObservable(() ->
                            Optional.ofNullable(getValue(cellData.getValue().getVolumeInfo()))
                                    .map(Object::toString)
                                    .orElse("-"))
            );
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

    private static final class IndexColumn extends Column<Integer> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(ColumnType.INDEX_COLUMN, false);
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

    private static final class TypeIndicatorColumn extends Column<String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        TypeIndicatorColumn() {
            super(ColumnType.TYPE_INDICATOR_COLUMN, false);
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
                        Volume.VolumeInfo volume = getVolumeInfo(this);
                        setGraphic(new MaterialDesignIconView(volume.isMagazine() ? MaterialDesignIcon.NEWSPAPER : MaterialDesignIcon.BOOK));
                        setTooltip(new Tooltip(I18N.getGoogleBooksImportValue(volume.isMagazine() ? "google.books.magazine" : "google.books.book")));
                    }
                }
            };
        }

    }

    private static final class ThumbnailColumn extends Column<String>
            implements Callback<TableColumn<Volume, String>, TableCell<Volume, String>> {

        ThumbnailColumn() {
            super(ColumnType.THUMBNAIL_COLUMN);
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

    @SuppressWarnings("DuplicatedCode")
    private static final class ISBN10Column extends Column<String> {

        ISBN10Column() {
            super(ColumnType.ISBN_10_COLUMN);
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
    private static final class ISBN13Column extends Column<String> {
        ISBN13Column() {
            super(ColumnType.ISBN_13_COLUMN);
            setSortable(false);
            setCellValueFactory(cellData -> BaseFXUtils.constantObservable(() ->
                    Optional.ofNullable(cellData.getValue())
                            .map(Volume::getVolumeInfo)
                            .map(Volume.VolumeInfo::getIndustryIdentifiers)
                            .flatMap(identifiers -> identifiers.stream()
                                    .filter(Volume.VolumeInfo.IndustryIdentifier::isIsbn13)
                                    .map(Volume.VolumeInfo.IndustryIdentifier::getIdentifier)
                                    .findAny()).orElse("-"))
            );
        }
    }

    private static final class ISBNColumn extends Column<String> {
        ISBNColumn() {
            super(ColumnType.ISBN_COLUMN);
            setSortable(false);
            setCellValueFactory(cellData ->
                    BaseFXUtils.constantObservable(() ->
                            Optional.ofNullable(cellData.getValue().getVolumeInfo())
                                    .map(Volume.VolumeInfo::getIndustryIdentifiersAsString)
                                    .orElse("-"))
            );
        }
    }

    private static final class AuthorColumn extends AbcSortableColumn {
        AuthorColumn() {
            super(ColumnType.AUTHOR_COLUMN);
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

    private static final class LangColumn extends Column<String> {
        LangColumn() {
            super(ColumnType.LANG_COLUMN);
            setCellValueFactory(cellData ->
                    BaseFXUtils.constantObservable(() ->
                            Optional.ofNullable(cellData.getValue().getVolumeInfo())
                                    .map(Volume.VolumeInfo::getLanguage)
                                    .map(Locale::forLanguageTag)
                                    .map(Locale::getDisplayLanguage)
                                    .orElse("-"))
            );
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

    private static final class RankColumn extends Column<String>
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

    private static final class BrowserColumn extends Column<String>
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
