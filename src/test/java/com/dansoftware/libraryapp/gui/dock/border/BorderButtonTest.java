package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BorderButtonTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderButton borderButton = new BorderButton(new Rectangle(20,20), "BorderBtn1");
        borderButton.setButtonOrientation(BorderButton.ButtonOrientation.HORIZONTAL_LEFT);

        Scene scene = new Scene(new StackPane(borderButton));
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
