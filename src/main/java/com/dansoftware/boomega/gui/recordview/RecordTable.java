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

package com.dansoftware.boomega.gui.recordview;

import com.dansoftware.boomega.database.api.data.Record;
import com.dansoftware.boomega.database.api.data.RecordProperty;
import com.dansoftware.boomega.database.api.data.ServiceConnection;
import com.dansoftware.boomega.gui.control.BaseTable;
import com.dansoftware.boomega.gui.control.ReadOnlyRating;
import com.dansoftware.boomega.gui.control.TableViewPlaceHolder;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.dansoftware.boomega.gui.control.BaseTable.ColumnType.*;
import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

public class RecordTable extends BaseTable<Record> {

    public static final ColumnType INDEX_COLUMN =
            new ColumnType(
                    "index",
                    i18n("record.table.column.index"),
                    RecordTable.class,
                    table -> new IndexColumn(table.startIndex),
                    DEFAULT_VISIBLE
            );

    public static final ColumnType TYPE_INDICATOR_COLUMN =
            buildColumnType(
                    RecordProperty.TYPE,
                    (col, table) -> new TypeIndicatorColumn(),
                    DEFAULT_VISIBLE
            );

    public static final ColumnType AUTHOR_COLUMN =
            buildColumnType(
                    RecordProperty.AUTHORS,
                    (col, table) -> new AuthorColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType LANG_COLUMN =
            buildColumnType(
                    RecordProperty.LANGUAGE,
                    (col, table) -> new LangColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType RANK_COLUMN =
            buildColumnType(
                    RecordProperty.RATING,
                    (col, table) -> new RankColumn(),
                    DEFAULT_VISIBLE,
                    TEXT_GUI_VISIBLE
            );

    public static final ColumnType SERVICE_CONNECTION_COLUMN =
            buildColumnType(
                    RecordProperty.SERVICE_CONNECTION,
                    (col, table) -> new ServiceConnectionColumn(),
                    DEFAULT_VISIBLE
            );

    public static final ColumnType MAGAZINE_NAME_COLUMN =
            buildColumnType(RecordProperty.MAGAZINE_NAME, TEXT_GUI_VISIBLE);

    public static final ColumnType TITLE_COLUMN =
            buildColumnType(RecordProperty.TITLE, DEFAULT_VISIBLE, TEXT_GUI_VISIBLE);

    public static final ColumnType SUB_TITLE_COLUMN =
            buildColumnType(RecordProperty.SUBTITLE, TEXT_GUI_VISIBLE);

    public static final ColumnType ISBN_COLUMN =
            buildColumnType(RecordProperty.ISBN, DEFAULT_VISIBLE, TEXT_GUI_VISIBLE);

    public static final ColumnType PUBLISHER_COLUMN =
            buildColumnType(RecordProperty.PUBLISHER, DEFAULT_VISIBLE, TEXT_GUI_VISIBLE);

    public static final ColumnType DATE_COLUMN =
            buildColumnType(RecordProperty.PUBLISHED_DATE, DEFAULT_VISIBLE, TEXT_GUI_VISIBLE);

    public static final ColumnType COPY_COUNT_COLUMN =
            buildColumnType(RecordProperty.NUMBER_OF_COPIES, TEXT_GUI_VISIBLE);

    private static final List<ColumnType> columnsList =
            List.of(
                    INDEX_COLUMN,
                    TYPE_INDICATOR_COLUMN,
                    AUTHOR_COLUMN,
                    MAGAZINE_NAME_COLUMN,
                    TITLE_COLUMN,
                    SUB_TITLE_COLUMN,
                    ISBN_COLUMN,
                    PUBLISHER_COLUMN,
                    DATE_COLUMN,
                    COPY_COUNT_COLUMN,
                    LANG_COLUMN,
                    RANK_COLUMN,
                    SERVICE_CONNECTION_COLUMN
            );

    private static ColumnType buildColumnType(@NotNull RecordProperty<?> recordProperty, Option... options) {
        return buildColumnType(
                recordProperty,
                (col, table) -> new SimplePropertyColumn(col, recordProperty),
                options
        );
    }

    private static ColumnType buildColumnType(@NotNull RecordProperty<?> recordProperty,
                                              @NotNull BiFunction<ColumnType, RecordTable, Column<?, ?>> columnFactory,
                                              Option... options) {
        return new ColumnType(
                recordProperty.getId(),
                recordProperty.getName(),
                RecordTable.class,
                columnFactory,
                options
        );
    }

    public static List<ColumnType> columns() {
        return columnsList;
    }

    public static Optional<ColumnType> columnById(@NotNull String id) {
        return columns().stream().filter(it -> it.getId().equals(id)).findAny();
    }

    private static final String STYLE_CLASS = "books-table";

    private final IntegerProperty startIndex;

    public RecordTable(int startIndex) {
        this.startIndex = new SimpleIntegerProperty(startIndex);
        this.getStyleClass().add(STYLE_CLASS);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        this.setPlaceholder(new PlaceHolder(this));
    }

    public IntegerProperty startIndexProperty() {
        return startIndex;
    }

    private static class PlaceHolder extends TableViewPlaceHolder {
        PlaceHolder(@NotNull RecordTable table) {
            super(table);
            setMinHeight(0);
        }

        @Nullable
        @Override
        protected Node contentIfEmpty() {
            VBox vBox = new VBox(5.0);
            vBox.getStyleClass().add("record-table-place-holder");
            vBox.getChildren().add(new StackPane(new ImageView()));
            vBox.getChildren().add(new StackPane(new Label(i18n("record.table.place.holder"))));
            return vBox;
        }

        @Nullable
        @Override
        protected Node contentIfNoColumns() {
            return new Label(i18n("record.table.place.holder.nocolumn"));
        }
    }

    private static class RecordTableCell<T> extends TableCell<Record, T> {

        @Override
        public final void updateIndex(int i) {
            super.updateIndex(i);
            if (i >= 0 && getTableView().getItems().size() > i) {
                updateItem(getTableView().getItems().get(i));
            } else {
                updateItem(null);
            }
        }

        protected void updateItem(@Nullable Record item) {
        }

    }

    private static final class IndexColumn extends Column<Record, Integer>
            implements Callback<TableColumn.CellDataFeatures<Record, Integer>, ObservableValue<Integer>> {
        private static final int COLUMN_WIDTH_UNIT = 60;

        private final IntegerProperty startIndexProperty;

        IndexColumn(IntegerProperty startIndexProperty) {
            super(INDEX_COLUMN);
            this.startIndexProperty = startIndexProperty;
            setReorderable(false);
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

    private static final class TypeIndicatorColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        TypeIndicatorColumn() {
            super(TYPE_INDICATOR_COLUMN);
            setReorderable(false);
            setCellFactory(this);
            setMinWidth(50);
            setMaxWidth(60);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    if (item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(new MaterialDesignIconView(
                                item.getType() == Record.Type.BOOK ?
                                        MaterialDesignIcon.BOOK : MaterialDesignIcon.NEWSPAPER));
                    }
                }
            };
        }
    }

    private static final class AuthorColumn extends SortableColumn<Record>
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        AuthorColumn() {
            super(AUTHOR_COLUMN);
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
                    return "-";
                }
            };
        }
    }

    private static final class SimplePropertyColumn extends SortableColumn<Record> {
        SimplePropertyColumn(@NotNull ColumnType columnType, @NotNull RecordProperty<?> property) {
            super(columnType);
            setCellValueFactory(new PropertyValueFactory<>(property.getId()) {
                @Override
                public ObservableValue<String> call(CellDataFeatures<Record, String> param) {
                    ObservableValue<String> observable = super.call(param);
                    Object value = observable.getValue();
                    return new ReadOnlyObjectWrapper<>(value != null ? value.toString() : "-");
                }
            });
        }
    }

    private static final class LangColumn extends Column<Record, String>
            implements Callback<TableColumn.CellDataFeatures<Record, String>, ObservableValue<String>> {
        LangColumn() {
            super(LANG_COLUMN);
            setCellValueFactory(this);
        }

        @Override
        public ObservableValue<String> call(CellDataFeatures<Record, String> cellData) {
            return new ObservableValueBase<String>() {
                @Override
                public String getValue() {
                    final Locale raw = cellData.getValue().getLanguage();
                    if (raw != null)
                        return raw.getDisplayLanguage();
                    return "-";
                }
            };
        }
    }

    private static final class RankColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {
        RankColumn() {
            super(RANK_COLUMN);
            setCellFactory(this);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    if (item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        ReadOnlyRating graphic = buildGraphic(Optional.ofNullable(item.getRating()).orElse(0));
                        setGraphic(graphic);
                    }
                }

                private ReadOnlyRating buildGraphic(int rating) {
                    return new ReadOnlyRating(5, rating);
                }
            };
        }
    }

    private static final class ServiceConnectionColumn extends Column<Record, String>
            implements Callback<TableColumn<Record, String>, TableCell<Record, String>> {

        private static final double WIDTH = 60;

        ServiceConnectionColumn() {
            super(SERVICE_CONNECTION_COLUMN);
            this.setReorderable(false);
            this.setCellFactory(this);
            this.setMinWidth(WIDTH);
            this.setMaxWidth(WIDTH);
        }

        @Override
        public TableCell<Record, String> call(TableColumn<Record, String> param) {
            return new RecordTableCell<>() {
                @Override
                protected void updateItem(@Nullable Record item) {
                    super.updateItem(item);
                    if (item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Optional.ofNullable(item.getServiceConnection())
                                .map(ServiceConnection::getGoogleBookHandle)
                                .map(value -> StringUtils.getIfBlank(value, null))
                                .ifPresentOrElse(
                                        value -> setGraphic(buildGoogleBooksIcon()),
                                        () -> setGraphic(null)
                                );
                    }
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };
        }

        private Node buildGoogleBooksIcon() {
            return new ImageView(new Image("/com/dansoftware/boomega/image/util/google_12px.png"));
        }
    }
}
