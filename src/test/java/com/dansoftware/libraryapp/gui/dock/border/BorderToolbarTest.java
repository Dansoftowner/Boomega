package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.dock.border.toolbar.BorderToolbar;
import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BorderToolbarTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderButton borderButton = new BorderButton(new Rectangle(20,20), "BorderBtn1");
        BorderButton borderButton1 = new BorderButton(new Rectangle(20,20), "BorderBtn2");
        BorderButton borderButton2 = new BorderButton(new Rectangle(20,20), "BorderBtn3");


        BorderToolbar toolbar = new BorderToolbar();
        toolbar.setToolbarOrientation(BorderToolbar.ToolbarOrientation.HORIZONTAL_RIGHT);
        toolbar.addBorderButton(borderButton);
        toolbar.addBorderButton(borderButton1);
        toolbar.addBorderButton(borderButton2);


        Scene scene = new Scene(toolbar);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
