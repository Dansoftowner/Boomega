package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.util.FileExplorer;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Objects;

public class DBManagerTable extends TableView<Account> {

    private final DBManagerView parent;

    public DBManagerTable(DBManagerView parent, ObservableList<Account> accounts) {
        this.parent = parent;
        this.init(accounts);
    }

    private void init(ObservableList<Account> accounts) {
        this.setItems(accounts);
        this.setPlaceholder(new Label(I18N.getGeneralWord("database.manager.table.place.holder")));
        this.getColumns().addAll(createColumns());
    }

    private TableColumn<Account, String>[] createColumns() {
        TableColumn<Account, String> nameColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.name"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("dbName"));

        TableColumn<Account, String> pathColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.path"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("file"));

        TableColumn<Account, String> openInExplorerColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.open"));
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
                        Account account = getTableView().getItems().get(getIndex());
                        FileExplorer.show(account.getFile());
                    });

                    setGraphic(openButton);
                }
            }
        });

        TableColumn<Account, String> deleteColumn =
                new TableColumn<>(I18N.getGeneralWord("database.manager.table.column.delete"));
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
                        Account account = getTableView().getItems().get(getIndex());

                        DBManagerTable.this.parent.showConfirmationDialog(
                                I18N.getAlertMsg("db.manager.table.confirm.delete.title"),
                                I18N.getAlertMsg("db.manager.table.confirm.delete.msg", account.getDbName()),
                                buttonType -> {
                                    if (Objects.equals(buttonType, ButtonType.YES)) {
                                        getTableView().getItems().remove(account);
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
