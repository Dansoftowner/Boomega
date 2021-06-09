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

package com.dansoftware.boomega.gui.control;

import com.dansoftware.boomega.i18n.I18N;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BoomegaTable<S> extends TableView<S> {

    private final ObjectProperty<Consumer<S>> onItemDoubleClicked;
    private final ObjectProperty<Consumer<S>> onItemSecondaryDoubleClicked;
    private final ObjectProperty<ContextMenu> rowContextMenu;

    private final ObservableList<ColumnType<? extends Column<?, ?>>> columnTypes;

    public BoomegaTable() {
        this.onItemDoubleClicked = new SimpleObjectProperty<>();
        this.onItemSecondaryDoubleClicked = new SimpleObjectProperty<>();
        this.rowContextMenu = new SimpleObjectProperty<>();
        this.columnTypes = buildColumnTypeList();
        this.buildClickHandlingPolicy();
    }

    private void buildClickHandlingPolicy() {
        this.setRowFactory(table -> buildTableRow());
    }

    private ObservableList<ColumnType<? extends Column<?, ?>>> buildColumnTypeList() {
        final ObservableList<ColumnType<? extends Column<?, ?>>> list = FXCollections.observableArrayList();
        list.addListener((ListChangeListener<ColumnType<? extends Column<?, ?>>>) c -> {
            while (c.next()) {
                if (c.wasAdded())
                    c.getAddedSubList().forEach(it -> {
                        boolean contains =
                                getColumns().stream()
                                        .map(Column.class::cast)
                                        .filter(col -> col.columnType.equals(it))
                                        .findAny()
                                        .orElse(null) == null;
                        if (!contains) {
                            getColumns().add((TableColumn<S, ?>) it.getColumnFactory().apply(this));
                        }
                    });
                else if (c.wasRemoved() || c.wasReplaced())
                    c.getRemoved().forEach(it -> getColumns().removeIf(column -> ((Column<S, ?>) column).columnType.equals(it)));
            }
        });
        return list;
    }

    private TableRow<S> buildTableRow() {
        TableRow<S> tableRow = new TableRow<>();
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
        tableRow.contextMenuProperty().bind(rowContextMenu);
        tableRow.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            if (tableRow.isEmpty())
                event.consume();
        });

        return tableRow;
    }

    public void removeAllColumns() {
        this.getColumns().clear();
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

    public ObservableList<ColumnType<? extends Column<?, ?>>> getColumnTypes() {
        return columnTypes;
    }

    public Consumer<S> getOnItemDoubleClicked() {
        return onItemDoubleClicked.get();
    }

    public ObjectProperty<Consumer<S>> onItemDoubleClickedProperty() {
        return onItemDoubleClicked;
    }

    public void setOnItemDoubleClicked(Consumer<S> onItemDoubleClicked) {
        this.onItemDoubleClicked.set(onItemDoubleClicked);
    }

    public Consumer<S> getOnItemSecondaryDoubleClicked() {
        return onItemSecondaryDoubleClicked.get();
    }

    public ObjectProperty<Consumer<S>> onItemSecondaryDoubleClickedProperty() {
        return onItemSecondaryDoubleClicked;
    }

    public void setOnItemSecondaryDoubleClicked(Consumer<S> onItemSecondaryDoubleClicked) {
        this.onItemSecondaryDoubleClicked.set(onItemSecondaryDoubleClicked);
    }

    public static class ColumnType<C extends Column<?, ?>> {

        public static final Option DEFAULT_VISIBLE = new Option();
        public static final Option TEXT_GUI_VISIBLE = new Option();
        public static final Option I18N = new Option();

        private final String text;
        private final Class<C> tableColumnClass;
        private final Function<BoomegaTable<?>, ? extends C> columnFactory;
        private final List<Option> options;

        public ColumnType(@NotNull String text,
                          @NotNull Class<C> tableColumnClass,
                          @NotNull Function<BoomegaTable<?>, ? extends C> columnFactory,
                          Option... options) {
            this.text = Objects.requireNonNull(text);
            this.tableColumnClass = Objects.requireNonNull(tableColumnClass);
            this.columnFactory = columnFactory;
            this.options = List.of(options);
        }

        public String getText() {
            return text;
        }

        public Class<C> getTableColumnClass() {
            return tableColumnClass;
        }

        public Function<BoomegaTable<?>, ? extends C> getColumnFactory() {
            return columnFactory;
        }

        public boolean isDefaultVisible() {
            return options.contains(DEFAULT_VISIBLE);
        }

        public boolean isTextOnUIVisible() {
            return options.contains(TEXT_GUI_VISIBLE);
        }

        public boolean isI18N() {
            return options.contains(I18N);
        }

        /**
         * Used for representing options/configurations for a {@link ColumnType}.
         */
        public static class Option {
            private Option() {
            }
        }
    }

    /**
     * A {@link TableColumn} implementation that should be used with a {@link BoomegaTable}.
     *
     * @see javafx.scene.control.TableColumn
     */
    public static class Column<S, T> extends TableColumn<S, T> {

        private final ColumnType<? extends Column<S, T>> columnType;

        public Column(@NotNull ColumnType<? extends Column<S, T>> columnType) {
            this.columnType = columnType;
            this.initToColumnType(columnType);
        }

        private void initToColumnType(ColumnType<? extends Column<S, T>> columnType) {
            setText(getTextFor(columnType));
        }

        private String getTextFor(ColumnType<? extends Column<S, T>> columnType) {
            return columnType.isTextOnUIVisible() ?
                    (columnType.isI18N() ? I18N.getValue(columnType.text) : columnType.text) : null;
        }

    }

    public static abstract class SortableColumn<S> extends Column<S, String> {

        public SortableColumn(@NotNull ColumnType<? extends Column<S, String>> columnType) {
            super(columnType);
            setSortable(true);
        }
    }
}
