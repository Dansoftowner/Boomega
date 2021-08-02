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

package com.dansoftware.boomega.gui.dbmanager;

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.util.ConcurrencyUtils;
import com.dansoftware.boomega.gui.util.FXCollectionUtils;
import com.dansoftware.boomega.gui.util.I18NButtonTypes;
import com.jfilegoodies.explorer.FileExplorers;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static com.dansoftware.boomega.gui.util.Icons.icon;
import static com.dansoftware.boomega.i18n.I18NUtils.i18n;

/**
 * A DBManagerTable is a {@link TableView} that is used for managing (monitoring, deleting) databases.
 *
 * <p>
 * It should only be used through a {@link DatabaseManagerView}.
 *
 * @author Daniel Gyorffy
 * @see DatabaseManagerView
 */
class DatabaseManagerTable extends TableView<DatabaseMeta>
        implements DatabaseTracker.Observer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagerTable.class);

    private final Context context;
    private final DatabaseTracker databaseTracker;

    private final IntegerBinding itemsCount;
    private final IntegerBinding selectedItemsCount;

    public DatabaseManagerTable(@NotNull Context context,
                                @NotNull DatabaseTracker databaseTracker) {
        this.context = context;
        this.databaseTracker = databaseTracker;
        this.databaseTracker.registerObserver(this);
        this.itemsCount = Bindings.size(getItems());
        this.selectedItemsCount = Bindings.size(getSelectionModel().getSelectedItems());
        this.init(databaseTracker.getSavedDatabases());
    }

    private void init(Collection<DatabaseMeta> databases) {
        this.getItems().addAll(databases);
        this.setPlaceholder(new Label(i18n("database.manager.table.place.holder")));
        Stream.of(
                new StateColumn(),
                new NameColumn(),
                new PathColumn(),
                new SizeColumn(),
                new FileOpenerColumn(),
                new DeleteColumn()
        ).forEach(this.getColumns()::add);

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public IntegerBinding selectedItemsCount() {
        return selectedItemsCount;
    }

    public IntegerBinding itemsCount() {
        return itemsCount;
    }

    @Override
    public void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
        ConcurrencyUtils.runOnUiThread(this::refresh);
    }

    @Override
    public void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
        ConcurrencyUtils.runOnUiThread(this::refresh);
    }

    @Override
    public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
        ConcurrencyUtils.runOnUiThread(() -> this.getItems().add(databaseMeta));
    }

    @Override
    public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        ConcurrencyUtils.runOnUiThread(() -> this.getItems().remove(databaseMeta));
    }


    /* ------------------- TABLE COLUMNS ---------------------- */

    /**
     * The state-column shows an error-mark (red circle) if the particular database does not exist.
     */
    private final class StateColumn extends TableColumn<DatabaseMeta, String>
            implements Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        StateColumn() {
            setReorderable(false);
            setSortable(false);
            setResizable(false);
            setPrefWidth(30);
            setCellFactory(this);
        }

        @Override
        public TableCell<DatabaseMeta, String> call(TableColumn<DatabaseMeta, String> tableColumn) {
            return new TableCell<>() {

                private static final String NOT_EXISTS_CLASS = "state-indicator-file-not-exists";
                private static final String USED_CLASS = "state-indicator-used";

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {

                        DatabaseMeta databaseMeta = getTableView().getItems().get(getIndex());
                        File dbFile = databaseMeta.getFile();
                        if (!dbFile.exists() || dbFile.isDirectory()) {
                            var indicator = icon("warning-icon");
                            indicator.getStyleClass().add(NOT_EXISTS_CLASS);
                            setGraphic(indicator);
                            getTableRow().setTooltip(new Tooltip(i18n("file.not.exists")));
                        } else if (DatabaseManagerTable.this.databaseTracker.isDatabaseUsed(databaseMeta)) {
                            var indicator = icon("play-icon");
                            indicator.getStyleClass().add(USED_CLASS);
                            setGraphic(indicator);
                            getTableRow().setTooltip(new Tooltip(i18n("database.currently.used")));
                        } else {
                            setGraphic(null);
                            getTableRow().setTooltip(null);
                        }

                    }
                }
            };
        }
    }

    /**
     * The name-column shows the name of the database.
     */
    private static final class NameColumn extends TableColumn<DatabaseMeta, String> {
        NameColumn() {
            super(i18n("database.manager.table.column.name"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    /**
     * The path-column shows the filepath of the database
     */
    private static final class PathColumn extends TableColumn<DatabaseMeta, String> {
        PathColumn() {
            super(i18n("database.manager.table.column.path"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("file"));
        }
    }

    /**
     * The size-column shows the file-size of the database
     */
    private static final class SizeColumn extends TableColumn<DatabaseMeta, String>
            implements Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        SizeColumn() {
            super(i18n("database.manager.table.column.size"));
            setReorderable(false);
            setCellFactory(this);
        }

        @Override
        public TableCell<DatabaseMeta, String> call(TableColumn<DatabaseMeta, String> tableColumn) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        DatabaseMeta databaseMeta = getTableView().getItems().get(getIndex());
                        File dbFile = databaseMeta.getFile();
                        if (dbFile.exists() && !dbFile.isDirectory()) {
                            long size = FileUtils.sizeOf(dbFile);
                            String readableSize = FileUtils.byteCountToDisplaySize(size);
                            setText(readableSize);
                        } else {
                            setText("-");
                        }
                    }
                }
            };
        }
    }

    /**
     * The file-opener-column provides a {@link Button} to open the database-file in the native
     * file-explorer.
     */
    private static final class FileOpenerColumn extends TableColumn<DatabaseMeta, String>
            implements Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        FileOpenerColumn() {
            super(i18n("file.open_in_explorer"));
            setMinWidth(90);
            setSortable(false);
            setReorderable(false);
            setCellFactory(this);
        }

        @Override
        public TableCell<DatabaseMeta, String> call(TableColumn<DatabaseMeta, String> tableColumn) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button openButton = new Button();
                        openButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        openButton.setGraphic(icon("folder-open-icon"));
                        openButton.prefWidthProperty().bind(getTableColumn().widthProperty());
                        openButton.setOnAction(event -> getTableView()
                                .getSelectionModel()
                                .getSelectedItems()
                                .stream()
                                .map(DatabaseMeta::getFile)
                                .forEach(FileExplorers.getLazy()::openSelect));
                        openButton.disableProperty().bind(getTableRow().selectedProperty().not());
                        setGraphic(openButton);
                    }
                }
            };
        }
    }

    /**
     * The delete-column provides a {@link Button} to delete the selected database(s).
     */
    private final class DeleteColumn extends TableColumn<DatabaseMeta, String>
            implements Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        DeleteColumn() {
            super(i18n("database.manager.table.column.delete"));
            setReorderable(false);
            setSortable(false);
            setMinWidth(90);
            setCellFactory(this);
        }

        @Override
        public TableCell<DatabaseMeta, String> call(TableColumn<DatabaseMeta, String> tableColumn) {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button deleteButton = new Button();
                        deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        deleteButton.setGraphic(icon("database-minus-icon"));
                        deleteButton.prefWidthProperty().bind(tableColumn.widthProperty());
                        deleteButton.setOnAction(event -> {
                            ObservableList<DatabaseMeta> selectedItems =
                                    FXCollectionUtils.copy(getTableView().getSelectionModel().getSelectedItems());
                            DBDeleteDialog dialog = new DBDeleteDialog();
                            dialog.show(selectedItems);
                        });
                        deleteButton.disableProperty().bind(this.getTableRow().selectedProperty().not());
                        setGraphic(deleteButton);
                    }
                }
            };
        }
    }

    /* -------------------- */

    /**
     * A DBDeleteDialog is used for showing database-deleting dialog.
     * It's used by the {@link DeleteColumn}.
     */
    private final class DBDeleteDialog {

        public void show(@NotNull ObservableList<DatabaseMeta> itemsToRemove) {
            DatabaseManagerTable.this.context.showDialog(
                    i18n("database.manager.confirm_delete.title", itemsToRemove.size()),
                    new ListView<>(itemsToRemove),
                    buttonType -> {
                        if (Objects.equals(buttonType, I18NButtonTypes.YES)) {
                            itemsToRemove.forEach(DatabaseManagerTable.this.databaseTracker::removeDatabase);
                        }
                    },
                    I18NButtonTypes.YES,
                    I18NButtonTypes.NO);
        }
    }
}
