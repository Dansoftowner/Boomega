package com.dansoftware.libraryapp.gui.info;

import com.dansoftware.libraryapp.locale.I18N;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class InfoView extends AnchorPane {

    public InfoView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("info-view.fxml"), I18N.getFXMLValues());

        try {
            AnchorPane root = loader.load();
            this.copy(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copy(AnchorPane anchorPane) {
        this.getChildren().add(anchorPane);
        this.setPrefWidth(anchorPane.getPrefWidth());
        this.setPrefHeight(anchorPane.getPrefHeight());
    }
}
