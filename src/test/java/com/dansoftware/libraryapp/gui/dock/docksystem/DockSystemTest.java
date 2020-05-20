package com.dansoftware.libraryapp.gui.dock.docksystem;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DockSystemTest extends Application {

    static {
        Theme.setDefault(Theme.DARK);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DockSystem<Node> dockSystem = new DockSystem<>();
        dockSystem.dock(DockPosition.TOP_LEFT, new DockNode(null , "Sanyi", new StackPane(new Label("Panel1"))));

        dockSystem.dock(DockPosition.RIGHT_TOP, new DockNode(null , "Sanyi", new StackPane(new Button("Panel2"))));

        Scene scene = new Scene(dockSystem);
        Theme.applyDefault(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
