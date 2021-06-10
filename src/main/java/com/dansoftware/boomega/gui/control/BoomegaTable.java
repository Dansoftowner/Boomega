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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BoomegaTable<S> extends TableView<S> {

    private final ObjectProperty<Consumer<S>> onItemDoubleClicked;
    private final ObjectProperty<Consumer<S>> onItemSecondaryDoubleClicked;
    private final ObjectProperty<ContextMenu> rowContextMenu;
    private final ObjectProperty<Comparator<String>> sortingComparator;

    public BoomegaTable() {
        this.onItemDoubleClicked = new SimpleObjectProperty<>();
        this.onItemSecondaryDoubleClicked = new SimpleObjectProperty<>();
        this.rowContextMenu = new SimpleObjectProperty<>();
        this.sortingComparator = new SimpleObjectProperty<>();
        this.buildClickHandlingPolicy();
    }

    private void buildClickHandlingPolicy() {
        this.setRowFactory(table -> buildTableRow());
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

    public boolean isColumnShown(@Nullable ColumnType columnType) {
        return getColumns().stream()
                .map(Column.class::cast)
                .map(col -> col.columnType)
                .anyMatch(col -> col.equals(columnType));
    }

    public List<ColumnType> getShowingColumnTypes() {
        return getColumns().stream()
                .map(Column.class::cast)
                .map(col -> col.columnType)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public void addColumnType(@NotNull ColumnType columnType) {
        getColumns().add((TableColumn<S, ?>) columnType.columnFactory.apply(this));
    }

    public void addColumnTypes(ColumnType... columnTypes) {
        Arrays.stream(columnTypes).forEach(this::addColumnType);
    }

    @SuppressWarnings("unchecked")
    public boolean removeColumnType(@Nullable ColumnType columnType) {
        return getColumns().removeIf(it -> ((Column<S, ?>) it).columnType.equals(columnType));
    }

    public void removeAllColumns() {
        this.getColumns().clear();
    }

    public Comparator<String> getSortingComparator() {
        return sortingComparator.get();
    }

    public ObjectProperty<Comparator<String>> sortingComparatorProperty() {
        return sortingComparator;
    }

    public void setSortingComparator(Comparator<String> sortingComparator) {
        this.sortingComparator.set(sortingComparator);
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

    /**
     * Represents a column in the particular {@link BoomegaTable}.
     */
    public static class ColumnType {

        public static final Option DEFAULT_VISIBLE = new Option();
        public static final Option TEXT_GUI_VISIBLE = new Option();
        public static final Option INTERNATIONALIZED = new Option();

        private final String id;
        private final String text;
        private final Function<BoomegaTable<?>, ? extends Column<?, ?>> columnFactory;
        private final List<Option> options;

        public ColumnType(@NotNull String id,
                          @NotNull String text,
                          @NotNull Function<BoomegaTable<?>, ? extends Column<?, ?>> columnFactory,
                          Option... options) {
            this.id = Objects.requireNonNull(id);
            this.text = Objects.requireNonNull(text);
            this.columnFactory = columnFactory;
            this.options = List.of(options);
        }

        @SuppressWarnings("unchecked")
        public <T extends BoomegaTable<?>> ColumnType(@NotNull String id,
                                                      @NotNull String text,
                                                      @SuppressWarnings("unused") @NotNull Class<T> tableClass,
                                                      @NotNull Function<T, ? extends Column<?, ?>> columnFactory,
                                                      Option... options) {
            this.id = Objects.requireNonNull(id);
            this.text = Objects.requireNonNull(text);
            this.columnFactory = (Function<BoomegaTable<?>, ? extends Column<?, ?>>) columnFactory;
            this.options = List.of(options);
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public Function<BoomegaTable<?>, ? extends Column<?, ?>> getColumnFactory() {
            return columnFactory;
        }

        public boolean isDefaultVisible() {
            return options.contains(DEFAULT_VISIBLE);
        }

        public boolean isTextOnUIVisible() {
            return options.contains(TEXT_GUI_VISIBLE);
        }

        public boolean isI18N() {
            return options.contains(INTERNATIONALIZED);
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

        private final ColumnType columnType;

        public Column(@NotNull ColumnType columnType) {
            this.columnType = columnType;
            this.setSortable(false);
            this.initToColumnType(columnType);
        }

        private void initToColumnType(ColumnType columnType) {
            setText(getTextFor(columnType));
        }

        private String getTextFor(ColumnType columnType) {
            return columnType.isTextOnUIVisible() ?
                    (columnType.isI18N() ? I18N.getValue(columnType.text) : columnType.text) : null;
        }

    }

    public static abstract class SortableColumn<S> extends Column<S, String> {

        public SortableColumn(@NotNull ColumnType columnType) {
            super(columnType);
            setSortable(true);
            bindToTableSoringComparator();
        }

        private void bindToTableSoringComparator() {
            tableViewProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends TableView<S>> observable, TableView<S> oldValue, TableView<S> table) {
                    if (table instanceof BoomegaTable<S> boomegaTable) {
                        comparatorProperty().bind(boomegaTable.sortingComparator);
                        observable.removeListener(this);
                    }
                }
            });
        }
    }
}
