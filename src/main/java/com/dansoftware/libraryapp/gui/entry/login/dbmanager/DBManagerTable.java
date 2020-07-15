package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.DBMeta;
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
 *
 */
public class DBManagerTable extends TableView<DBMeta> {

    private final DBManagerView parent;
    private final List<DBMeta> databaseList;

    public DBManagerTable(@NotNull DBManagerView parent,
                          @NotNull List<DBMeta> databaseList) {
        this.parent = parent;
        this.databaseList = databaseList;
        this.init(databaseList);
    }

    private void init(List<DBMeta> databases) {
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

    private WorkbenchDialog createDBDeleteDialog(@NotNull ObservableList<DBMeta> databasesToRemove) {
        ListView<DBMeta> listView = new ListView<>(databasesToRemove);

        return WorkbenchDialog.builder(
                I18N.getAlertMsg("db.manager.table.confirm.delete.title", databasesToRemove.size()),
                listView,
                ButtonType.YES,
                ButtonType.NO
        ).onResult(buttonType -> {
            if (Objects.equals(buttonType, ButtonType.YES)) {
                this.databaseList.removeAll(databasesToRemove);
                this.getItems().removeAll(databasesToRemove);
            }
        }).build();
    }

    private class StateColumn extends TableColumn<DBMeta, String> {
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

                        DBMeta dbMeta = getTableRow().getItem();
                        File dbFile = dbMeta.getFile();
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

    private class NameColumn extends TableColumn<DBMeta, String> {
        NameColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.name"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    private class PathColumn extends TableColumn<DBMeta, String> {
        PathColumn() {
            super(I18N.getGeneralWord("database.manager.table.column.path"));
            setReorderable(false);
            setCellValueFactory(new PropertyValueFactory<>("file"));
        }
    }

    private class SizeColumn extends TableColumn<DBMeta, String> {
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
                        DBMeta dbMeta = getTableRow().getItem();
                        File dbFile = dbMeta.getFile();
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

    private class FileOpenerColumn extends TableColumn<DBMeta, String> {
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
                                .map(DBMeta::getFile)
                                .forEach(FileExplorer::show));
                        openButton.disableProperty().bind(getTableRow().selectedProperty().not());
                        setGraphic(openButton);
                    }
                }
            });
        }
    }

    private class DeleteColumn extends TableColumn<DBMeta, String> {
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
                            ObservableList<DBMeta> selectedItems = getTableView().getSelectionModel().getSelectedItems();
                            DBManagerTable.this.parent.showDialog(DBManagerTable.this.createDBDeleteDialog(selectedItems));
                        });
                        deleteButton.disableProperty().bind(this.getTableRow().selectedProperty().not());
                        setGraphic(deleteButton);
                    }
                }
            });
        }
    }
}
