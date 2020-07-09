package com.dansoftware.libraryapp.gui.entry.login;

import com.dansoftware.libraryapp.db.Account;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Objects;

@Deprecated
final class SourceChooserItem extends ListCell<Account> {
    @Override
    protected void updateItem(Account item, boolean empty) {
        super.updateItem(item, empty);
        if (Objects.isNull(item) || empty) {
            setText(null);
            setGraphic(null);
        } else {
            Label nameLabel = new Label(item.getDbName());

            Label pathLabel = new Label("(" + item.getFile().getAbsolutePath() + ")");
            pathLabel.setTextFill(Color.GRAY);
            pathLabel.setStyle("-fx-font-style: italic");

            setGraphic(new HBox(10, nameLabel, pathLabel));
        }
    }
}