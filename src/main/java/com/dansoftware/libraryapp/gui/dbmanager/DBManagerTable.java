package com.dansoftware.libraryapp.gui.dbmanager;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.FileExplorer;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * A DBManagerTable is a {@link TableView} that is used for managing (monitoring, deleting) databases.
 *
 * <p>
 * It should only be used through a {@link DBManagerView}.
 *
 * @see DBManagerView
 * @author Daniel Gyorffy
 */
public class DBManagerTable extends TableView<DatabaseMeta> {

    private final DBManagerView parent;
    private final List<DatabaseMeta> databaseList;

    public DBManagerTable(@NotNull DBManagerView parent,
                          @NotNull List<DatabaseMeta> databaseList) {
        this.parent = parent;
        this.databaseList = databaseList;
        this.init(databaseList);
    }

    private void init(List<DatabaseMeta> databases) {
        this.getItems().addAll(databases);
        this.setPlaceholder(new Label(I18N.getGeneralWord("database.manager.table.place.holder")));
        this.getColumns().addAll(
                new StateColumn(),
                new NameColumn(),
                new PathColumn(),
                new SizeColumn(),
                new FileOpenerColumn(),
                new DeleteColumn()
        );
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    /* ------------------- TABLE COLUMNS ---------------------- s*/

    /**
     * The state-column shows an error-mark (red circle) if the particular database does not exist.
     */
    private static final class StateColumn extends TableColumn<DatabaseMeta, String> {
        StateColumn() {
            setReorderable(false);
            setSortable(false);
            setResizable(false);
            setPrefWidth(25);
            setCellFactory(tableColumn -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                        setTooltip(null);
                    } else {
                        Circle stateCircle = new Circle(5);

                        DatabaseMeta databaseMeta = getTableRow().getItem();
                        File dbFile = databaseMeta.getFile();
                        if (!dbFile.exists() || dbFile.isDirectory()) {
                            stateCircle.getStyleClass().add("state-circle-error");
                            setTooltip(new Tooltip(I18N.getGeneralWord("database.manager.table.column.state.error.not.exists")));
                        } else {
                            stateCircle.getStyleClass().add("state-circle-ok");
                            setTooltip(null);
                        }

                        setGraphic(stateCircle);
                    }
                }
            });
        }
    }

    /**
     * The name-column shows the name of the database.
     */
    private static final class NameColumn extends TableColumn<DatabaseMeta, String> {
        NameColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.name"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    /**
     * The path-column shows the filepath of the database
     */
    private static final class PathColumn extends TableColumn<DatabaseMeta, String> {
        PathColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.path"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("file"));
        }
    }

    /**
     * The size-column shows the file-size of the database
     */
    private static final class SizeColumn extends TableColumn<DatabaseMeta, String> {
        SizeColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.size"));
            setReorderable(false);
            setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        DatabaseMeta databaseMeta = getTableRow().getItem();
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
            });
        }
    }

    /**
     * The file-opener-column provides a {@link Button} to open the database-file in the native
     * file-explorer.
     */
    private static final class FileOpenerColumn extends TableColumn<DatabaseMeta, String> {
        FileOpenerColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.open"));
            setMinWidth(90);
            setSortable(false);
            setReorderable(false);
            setCellFactory(tableColumn -> new TableCell<>() {
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
                                .forEach(FileExplorer::show));
                        openButton.disableProperty().bind(getTableRow().selectedProperty().not());
                        setGraphic(openButton);
                    }
                }
            });
        }
    }

    /**
     * The delete-column provides a {@link Button} to delete the selected database(s).
     */
    private final class DeleteColumn extends TableColumn<DatabaseMeta, String> {
        DeleteColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.delete"));
            setReorderable(false);
            setSortable(false);
            setMinWidth(90);
            setCellFactory(tableColumn -> new TableCell<>() {
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
                            ObservableList<DatabaseMeta> selectedItems = getTableView().getSelectionModel().getSelectedItems();
                            DBDeleteDialog dialog = new DBDeleteDialog();
                            dialog.show(selectedItems);
                        });
                        deleteButton.disableProperty().bind(this.getTableRow().selectedProperty().not());
                        setGraphic(deleteButton);
                    }
                }
            });
        }
    }

    /* -------------------- */

    /**
     * A DBDeleteDialog is used for showing database-deleting dialog.
     * It's used by the {@link DeleteColumn}.
     */
    private final class DBDeleteDialog {

        public void show(@NotNull ObservableList<DatabaseMeta> itemsToRemove) {
            DBManagerTable.this.parent.showDialog(WorkbenchDialog.builder(
                    I18N.getAlertMsg("db.manager.table.confirm.delete.title", itemsToRemove.size()),
                    new ListView<>(itemsToRemove),
                    ButtonType.YES,
                    ButtonType.NO
            ).onResult(buttonType -> {
                if (Objects.equals(buttonType, ButtonType.YES)) {
                    DBManagerTable.this.databaseList.removeAll(itemsToRemove);
                    DBManagerTable.this.getItems().removeAll(itemsToRemove);
                }
            }).build());
        }
    }
}
