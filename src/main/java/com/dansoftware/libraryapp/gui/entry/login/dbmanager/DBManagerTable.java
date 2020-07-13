package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.DBMeta;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.FileExplorer;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 */
public class DBManagerTable extends TableView<DBMeta> {

    private final DBManagerView parent;

    public DBManagerTable(@NotNull DBManagerView parent,
                          @NotNull ObservableList<DBMeta> databases) {
        this.parent = parent;
        this.init(databases);
    }

    private void init(ObservableList<DBMeta> databases) {
        this.setItems(databases);
        this.setPlaceholder(new Label(I18N.getGeneralWord("database.manager.table.place.holder")));
        this.getColumns().addAll(createColumns());
    }

    /**
     * Creates all columns for the table
     *
     * @return the {@link TableColumn} objects inside an array
     */
    @SuppressWarnings("unchecked")
    private TableColumn<DBMeta, String>[] createColumns() {
        TableColumn<DBMeta, String> nameColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<DBMeta, String> pathColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.path"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("file"));

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
                    openButton.setOnAction(event -> {
                        DBMeta dbMeta = getTableView().getItems().get(getIndex());
                        FileExplorer.show(dbMeta.getFile());
                    });

                    setGraphic(openButton);
                }
            }
        });


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
                        DBMeta dbMeta = getTableView().getItems().get(getIndex());

                        DBManagerTable.this.parent.showConfirmationDialog(
                                I18N.getAlertMsg("db.manager.table.confirm.delete.title"),
                                I18N.getAlertMsg("db.manager.table.confirm.delete.msg", dbMeta.getName()),
                                buttonType -> {
                                    if (Objects.equals(buttonType, ButtonType.YES)) {
                                        getTableView().getItems().remove(dbMeta);
                                    }
                                }
                        );
                    });

                    setGraphic(deleteButton);
                }
            }
        });

        return new TableColumn[]{nameColumn, pathColumn, openInExplorerColumn, deleteColumn};
    }
}
