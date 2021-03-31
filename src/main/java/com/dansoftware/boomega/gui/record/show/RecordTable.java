package com.dansoftware.boomega.gui.record.show;

import com.dansoftware.boomega.db.data.Record;
import com.dansoftware.boomega.db.data.ServiceConnection;
import com.dansoftware.boomega.gui.control.ReadOnlyRating;
import com.dansoftware.boomega.gui.control.TableViewPlaceHolder;
import com.dansoftware.boomega.i18n.I18N;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Rating;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecordTable extends TableView<Record> {

    public enum ColumnType {
        INDEX_COLUMN("record.table.column.index", IndexColumn.class, true, table -> new IndexColumn(table.startIndex)),
        TYPE_INDICATOR_COLUMN("record.table.column.typeindicator", TypeIndicatorColumn.class, true, table -> new TypeIndicatorColumn()),
        AUTHOR_COLUMN("record.table.column.author", AuthorColumn.class, true, table -> new AuthorColumn()),
        MAGAZINE_NAME_COLUMN("record.table.column.magazinename", MagazineNameColumn.class, false, table -> new MagazineNameColumn()),
        TITLE_COLUMN("record.table.column.title", TitleColumn.class, true, table -> new TitleColumn()),
        SUB_TITLE_COLUMN("record.table.column.subtitle", SubtitleColumn.class, false, table -> new SubtitleColumn()),
        ISBN_COLUMN("record.table.column.isbn", ISBNColumn.class, true, table -> new ISBNColumn()),
        PUBLISHER_COLUMN("record.table.column.publisher", PublisherColumn.class, true, table -> new PublisherColumn()),
        DATE_COLUMN("record.table.column.date", DateColumn.class, true, table -> new DateColumn()),
        COPY_COUNT_COLUMN("record.table.column.copycount", CopyCountColumn.class, false, table -> new CopyCountColumn()),
        LANG_COLUMN("record.table.column.lang", LangColumn.class, true, table -> new LangColumn()),
        RANK_COLUMN("record.table.column.rank", RankColumn.class, true, table -> new RankColumn()),
        SERVICE_CONNECTION_COLUMN("record.table.column.service", ServiceConnectionColumn.class, true, table -> new ServiceConnectionColumn());

        private final String i18n;
        private final Class<? extends Column<?>> tableColumnClass;
        private final boolean defaultVisible;
        private final Function<RecordTable, ? extends Column<?>> createPolicy;

        <T extends Column<?>> ColumnType(String i18n,
                                         Class<T> tableColumnClass,
                                         boolean defaultVisible,
                                         Function<RecordTable, T> createPolicy) {
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

    private final ObjectProperty<ContextMenu> rowContextMenu = new SimpleObjectProperty<>();
    private final IntegerProperty startIndex;

    public RecordTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.init();
    }

    private void init() {
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.initRowFactory();
        this.setPlaceholder(
                new TableViewPlaceHolder(
                        this,
                        () -> I18N.getValue("record.table.place.holder"),
                        () -> I18N.getValue("record.table.place.holder.nocolumn")
                )
        );
    }

    private void initRowFactory() {
        this.setRowFactory(p -> {
            var row = new TableRow<Record>();
            row.contextMenuProperty().bind(rowContextMenu);
            row.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                if (row.isEmpty())
                    event.consume();
            });
            return row;
        });
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
                           Function<RecordTable, ? extends Column<?>> createAction) {
        this.getColumns().add(createAction.apply(this));
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    public ContextMenu getRowContextMenu() {
        return rowContextMenu.get();
    }

    public ObjectProperty<ContextMenu> rowContextMenuProperty() {
        return rowContextMenu;
    }

    public void setRowContextMenu(ContextMenu rowContextMenu) {
        this.rowContextMenu.set(rowContextMenu);
    }

    private static class Column<T> extends TableColumn<Record, T> {
        private final ColumnType columnType;

        Column(@NotNull ColumnType columnType, boolean i18n) {
            this.columnType = Objects.requireNonNull(columnType);
            this.setReorderable(false);
            if (i18n) setText(I18N.getValue(columnType.getI18Nkey()));
        }

        public Column(@NotNull ColumnType columnType) {
            this(columnType, true);
        }

        protected Record getRecordAtPosition(TableCell<Record, T> tableCell) {
            return getTableView().getItems().get(tableCell.getIndex());
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
            implements Callback<TableColumn.CellDataFeatures<Record, Integer>, ObservableValue<Integer>> {
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
        public ObservableValue<Integer> call(CellDataFeatures<Record, Integer> cellData) {
            return new ObservableValueBase<>() {
                @Override
                public Integer getValue() {
                    return startIndexProperty.get() +
                            cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1;
                }
            };
        }
    }

    private static final class TypeIndicatorColumn extends Column<String> implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        TypeIndicatorColumn() {
            super(ColumnType.TYPE_INDICATOR_COLUMN, false);
            setCellFactory(this);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {
                        setGraphic(new MaterialDesignIconView(
                                getRecordAtPosition(this).getRecordType() == Record.Type.BOOK ?
                                        MaterialDesignIcon.BOOK : MaterialDesignIcon.NEWSPAPER));
                        //TODO: TOOLTIP (BOOK/MAGAZINE)
                    }
                }
            };
        }
    }

    private static final class AuthorColumn extends AbcSortableColumn
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        AuthorColumn() {
            super(ColumnType.AUTHOR_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Record, String> cellData) {
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

    private static final class MagazineNameColumn extends AbcSortableColumn {
        MagazineNameColumn() {
            super(ColumnType.MAGAZINE_NAME_COLUMN);
            setCellValueFactory(new PropertyValueFactory<>("magazineName"));
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
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        LangColumn() {
            super(ColumnType.LANG_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Record, String> cellData) {
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

    private static final class RankColumn extends Column<String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        RankColumn() {
            super(ColumnType.RANK_COLUMN);
            setCellFactory(this);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Record record = getRecordAtPosition(this);
                        Optional.ofNullable(record.getRating())
                                .ifPresentOrElse(rating -> {
                                    Rating graphic = buildGraphic(rating);
                                    setGraphic(graphic);
                                    setText(null);
                                    RankColumn.this.minWidthProperty().bind(graphic.widthProperty().add(25));
                                    RankColumn.this.maxWidthProperty().bind(graphic.widthProperty().add(25));
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

    private static final class ServiceConnectionColumn extends Column<String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {

        private static final double WIDTH = 60;

        ServiceConnectionColumn() {
            super(ColumnType.SERVICE_CONNECTION_COLUMN, false);
            this.setCellFactory(this);
            this.setMinWidth(WIDTH);
            this.setMaxWidth(WIDTH);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Record record = getRecordAtPosition(this);
                        Optional.ofNullable(record.getServiceConnection())
                                .map(ServiceConnection::getGoogleBookLink)
                                .map(value -> StringUtils.getIfBlank(value, null))
                                .ifPresentOrElse(
                                        value -> setGraphic(buildGoogleBooksIcon()),
                                        () -> setGraphic(null)
                                );
                    }
                }
            };
        }

        private Node buildGoogleBooksIcon() {
            return new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png"));
        }
    }
}
