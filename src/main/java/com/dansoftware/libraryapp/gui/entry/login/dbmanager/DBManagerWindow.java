package com.dansoftware.libraryapp.gui.entry.login.dbmanager;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;
import java.util.Objects;

public class DBManagerWindow extends Stage {

    private static final double WIDTH = 900;
    private static final double HEIGHT = 430;

    public DBManagerWindow(DBManagerView view, Window owner) {
        this.setScene(new Scene(view));
        this.initModality(Modality.APPLICATION_MODAL);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.centerOnScreen();

        if (Objects.nonNull(owner)) {
            this.initOwner(owner);

            //Applying stylesheets from the owner-window
            List<String> styleSheets = owner.getScene()
                    .getRoot()
                    .getStylesheets();

            this.getScene().getStylesheets().addAll(styleSheets);
        }
    }
}
