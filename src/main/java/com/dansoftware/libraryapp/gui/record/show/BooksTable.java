package com.dansoftware.libraryapp.gui.record.show;

import com.dansoftware.libraryapp.db.data.Book;
import com.dansoftware.libraryapp.gui.googlebooks.GoogleBooksTable;
import com.dansoftware.libraryapp.gui.util.ReadOnlyRating;
import com.dansoftware.libraryapp.gui.util.TableViewPlaceHolder;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BooksTable extends TableView<Book> {

    public enum ColumnType {
        INDEX_COLUMN("books.table.column.index", IndexColumn.class, true, table -> new IndexColumn(table.startIndex)),
        AUTHOR_COLUMN("books.table.column.author", AuthorColumn.class, true, table -> new AuthorColumn()),
        TITLE_COLUMN("books.table.column.title", TitleColumn.class, true, table -> new TitleColumn()),
        SUB_TITLE_COLUMN("books.table.column.subtitle", SubtitleColumn.class, false, table -> new SubtitleColumn()),
        ISBN_COLUMN("books.table.column.isbn", ISBNColumn.class, true, table -> new ISBNColumn()),
        PUBLISHER_COLUMN("books.table.column.publisher", PublisherColumn.class, true, table -> new PublisherColumn()),
        DATE_COLUMN("books.table.column.date", DateColumn.class, true, table -> new DateColumn()),
        COPY_COUNT_COLUMN("books.table.column.copycount", CopyCountColumn.class, false, table -> new CopyCountColumn()),
        PAGE_COUNT_COLUMN("books.table.column.pagecount", PageCountColumn.class, false, table -> new PageCountColumn()),
        LANG_COLUMN("books.table.column.lang", LangColumn.class, true, table -> new LangColumn()),
        RANK_COLUMN("books.table.column.rank", RankColumn.class, true, table -> new RankColumn());

        private final String i18n;
        private final Class<? extends Column<?>> tableColumnClass;
        private final boolean defaultVisible;
        private final Function<BooksTable, ? extends Column<?>> createPolicy;

        <T extends Column<?>> ColumnType(String i18n,
                                         Class<T> tableColumnClass,
                                         boolean defaultVisible,
                                         Function<BooksTable, T> createPolicy) {
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

    private static final String STYLE_CLASS = "books-table";

    private final IntegerProperty startIndex;

    public BooksTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.init();
    }

    private void init() {
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(
                new TableViewPlaceHolder(
                    this,
                        () -> I18N.getBookViewValue("books.table.place.holder"),
                        () -> I18N.getBookViewValue("books.table.place.holder.nocolumn")
                )
        );
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
                           Function<BooksTable, ? extends Column<?>> createAction) {
        this.getColumns().add(createAction.apply(this));
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    private static class Column<T> extends TableColumn<Book, T> {
        private final ColumnType columnType;

        Column(@NotNull ColumnType columnType, boolean i18n) {
            this.columnType = Objects.requireNonNull(columnType);
            this.setReorderable(false);
            if (i18n) setText(I18N.getBookViewValue(columnType.getI18Nkey()));
        }

        public Column(@NotNull ColumnType columnType) {
            this(columnType, true);
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

    private static final class IndexColumn extends Column<Integer>
            implements Callback<TableColumn.CellDataFeatures<Book, Integer>, ObservableValue<Integer>> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(ColumnType.INDEX_COLUMN, false);
            this.startIndexProperty = startIndexProperty;
            setSortable(false);
            setMinWidth(COLUMN_WIDTH_UNIT);
            setMaxWidth(COLUMN_WIDTH_UNIT);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<Integer> call(CellDataFeatures<Book, Integer> cellData) {
            return new ObservableValueBase<>() {
                @Override
                public Integer getValue() {
                    return startIndexProperty.get() +
                            cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1;
                }
            };
        }
    }

    private static final class AuthorColumn extends AbcSortableColumn
            implements Callback<TableColumn.CellDataFeatures<Book, String>, ObservableValue<String>> {
        AuthorColumn() {
            super(ColumnType.AUTHOR_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Book, String> cellData) {
            return new ObservableValueBase<>() {
                @Override
                public String getValue() {
                    final List<String> authors = cellData.getValue().getAuthors();
                    if (authors != null)
                        return String.join(", ", authors);
                    return StringUtils.EMPTY;
                }
            };
        }
    }

    private static final class TitleColumn extends AbcSortableColumn {
        TitleColumn() {
            super(ColumnType.TITLE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("title"));
        }
    }

    private static final class SubtitleColumn extends AbcSortableColumn {
        SubtitleColumn() {
            super(ColumnType.SUB_TITLE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("subtitle"));
        }
    }

    private static final class PublisherColumn extends AbcSortableColumn {
        PublisherColumn() {
            super(ColumnType.PUBLISHER_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("publisher"));
        }
    }

    private static final class LangColumn extends Column<String>
            implements Callback<TableColumn.CellDataFeatures<Book, String>, ObservableValue<String>> {
        LangColumn() {
            super(ColumnType.LANG_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Book, String> cellData) {
            return new ObservableValueBase<String>() {
                @Override
                public String getValue() {
                    final String raw = cellData.getValue().getLanguage();
                    if (raw != null)
                        return Locale.forLanguageTag(raw).getDisplayLanguage();
                    return StringUtils.EMPTY;
                }
            };
        }
    }

    private static final class DateColumn extends Column<String> {
        DateColumn() {
            super(ColumnType.DATE_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        }
    }

    private static final class ISBNColumn extends Column<String> {
        ISBNColumn() {
            super(ColumnType.ISBN_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("isbn"));
        }
    }

    private static final class CopyCountColumn extends Column<Integer> {
        CopyCountColumn() {
            super(ColumnType.COPY_COUNT_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("numberOfCopies"));
        }
    }

    private static final class PageCountColumn extends Column<Integer> {
        PageCountColumn() {
            super(ColumnType.PAGE_COUNT_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("numberOfPages"));
        }
    }

    private static final class RankColumn extends Column<String>
            implements Callback<TableColumn<Book, String>, TableCell<Book, String>> {
        RankColumn() {
            super(ColumnType.RANK_COLUMN);
            setCellFactory(this);
        }

        @Override
        public TableCell<Book, String> call(TableColumn<Book, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Book book = getTableView().getItems().get(getIndex());
                        Optional.ofNullable(book.getRating())
                                .ifPresentOrElse(rating -> {
                                    Rating graphic = buildGraphic(rating);
                                    setGraphic(graphic);
                                    setText(null);
                                    RankColumn.this.minWidthProperty().bind(graphic.widthProperty());
                                }, () -> {
                                    setGraphic(null);
                                    setText("-");
                                });
                    }
                }

                private Rating buildGraphic(int rating) {
                    return new ReadOnlyRating(5, rating);
                }
            };
        }
    }
}
