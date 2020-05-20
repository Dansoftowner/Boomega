package com.dansoftware.libraryapp.gui.dock.docknode;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DockNodeTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DockNode dockNode = new DockNode("DockNode - Test dock node");
        dockNode.setCenter(new StackPane(new Button("OK")));

        Scene scene = new Scene(dockNode);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
