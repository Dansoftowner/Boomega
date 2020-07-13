package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.DBMeta;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.FileExplorer;
import com.dlsc.workbenchfx.model.WorkbenchDialog;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.collections.ListUtils;
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
        this.getColumns().addAll(createColumns());
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Creates all columns for the table
     *
     * @return the {@link TableColumn} objects inside an array
     */
    @SuppressWarnings("unchecked")
    private TableColumn<DBMeta, String>[] createColumns() {

        //tableColumn that displays the name of the database
        TableColumn<DBMeta, String> nameColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //tableColumn that displays the file-path
        TableColumn<DBMeta, String> pathColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.path"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("file"));

        TableColumn<DBMeta, String> sizeColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.size"));
        sizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    DBMeta dbMeta = getTableView().getItems().get(getIndex());
                    File dbFile = dbMeta.getFile();
                    long size = FileUtils.sizeOf(dbFile);
                    String readableSize = FileUtils.byteCountToDisplaySize(size);
                    setText(readableSize);
                }
            }
        });

        //tableColumn that provides a button to open the database file in the native explorer
        TableColumn<DBMeta, String> openInExplorerColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.open"));
        openInExplorerColumn.setSortable(Boolean.FALSE);
        openInExplorerColumn.setCellFactory(tableColumn -> new TableCell<>() {
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
                    openButton.prefWidthProperty().bind(tableColumn.widthProperty());
                    openButton.setOnAction(event -> this.getTableView()
                            .getSelectionModel()
                            .getSelectedItems()
                            .stream()
                            .map(DBMeta::getFile)
                            .forEach(FileExplorer::show));

                    openButton.disableProperty().bind(this.getTableRow().selectedProperty().not());
                    setGraphic(openButton);
                }
            }
        });

        //tableColumn that provides a button to delete database
        TableColumn<DBMeta, String> deleteColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.delete"));
        deleteColumn.setSortable(Boolean.FALSE);
        deleteColumn.setCellFactory(tableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button deleteButton = new Button();
                    deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    deleteButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE));
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

        return new TableColumn[]{nameColumn, pathColumn, sizeColumn, openInExplorerColumn, deleteColumn};
    }

    private WorkbenchDialog createDBDeleteDialog(ObservableList<DBMeta> databasesToRemove) {
        ListView<DBMeta> listView = new ListView<>(databasesToRemove);

        return WorkbenchDialog.builder(
                I18N.getAlertMsg("db.manager.table.confirm.delete.title"),
                listView,
                ButtonType.YES,
                ButtonType.NO
        ).onResult(buttonType -> {
            if (Objects.equals(buttonType, ButtonType.YES)) {
                this.getItems().removeAll(databasesToRemove);
                this.databaseList.removeAll(databasesToRemove);
            }
        }).build();
    }
}
