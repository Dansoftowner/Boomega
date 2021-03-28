package com.dansoftware.boomega.gui.dbmanager;

import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.util.BaseFXUtils;
import com.dansoftware.boomega.gui.util.FXCollectionUtils;
import com.dansoftware.boomega.gui.util.I18NButtonTypes;
import com.dansoftware.boomega.gui.util.UIUtils;
import com.dansoftware.boomega.i18n.I18N;
import com.jfilegoodies.explorer.FileExplorers;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

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
        this.getStyleClass().add(JMetroStyleClass.ALTERNATING_ROW_COLORS);
        this.setPlaceholder(new Label(I18N.getValue("database.manager.table.place.holder")));
        Stream.of(
                new StateColumn(),
                new NameColumn(),
                new PathColumn(),
                new SizeColumn(),
                new FileOpenerColumn(),
                new DeleteColumn()
        ).forEach(this.getColumns()::add);

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public IntegerBinding selectedItemsCount() {
        return selectedItemsCount;
    }

    public IntegerBinding itemsCount() {
        return itemsCount;
    }

    @Override
    public void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(this::refresh);
    }

    @Override
    public void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
        UIUtils.runOnUiThread(this::refresh);
    }

    @Override
    public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
        BaseFXUtils.runOnUiThread(() -> this.getItems().add(databaseMeta));
    }

    @Override
    public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        BaseFXUtils.runOnUiThread(() -> this.getItems().remove(databaseMeta));
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
                            var indicator = new FontAwesomeIconView(FontAwesomeIcon.WARNING);
                            indicator.getStyleClass().add(NOT_EXISTS_CLASS);
                            setGraphic(indicator);
                            getTableRow().setTooltip(new Tooltip(I18N.getValue("file.not.exists")));
                        } else if (DatabaseManagerTable.this.databaseTracker.isDatabaseUsed(databaseMeta)) {
                            var indicator = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                            indicator.getStyleClass().add(USED_CLASS);
                            setGraphic(indicator);
                            getTableRow().setTooltip(new Tooltip(I18N.getValue("database.currently.used")));
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
            super(I18N.getValue("database.manager.table.column.name"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    /**
     * The path-column shows the filepath of the database
     */
    private static final class PathColumn extends TableColumn<DatabaseMeta, String> {
        PathColumn() {
            super(I18N.getValue("database.manager.table.column.path"));
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
            super(I18N.getValue("database.manager.table.column.size"));
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
            super(I18N.getValue("database.manager.table.column.open"));
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
                        openButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.FOLDER));
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
            super(I18N.getValue("database.manager.table.column.delete"));
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
                        deleteButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DATABASE_MINUS));
                        deleteButton.prefWidthProperty().bind(tableColumn.widthProperty());
                        deleteButton.setOnAction(event -> {
                            ObservableList<DatabaseMeta> selectedItems =
                                    FXCollectionUtils.copyOf(getTableView().getSelectionModel().getSelectedItems());
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
                    I18N.getValue("db.manager.table.confirm.delete.title", itemsToRemove.size()),
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
