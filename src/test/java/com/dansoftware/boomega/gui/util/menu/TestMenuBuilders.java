package com.dansoftware.boomega.gui.util.menu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestMenuBuilders extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MenuBar menuBar = new MenuBarBuilder()
                .menu(new MenuBuilder()
                        .text("A")
                        .menuItem(new MenuItem("A1"))
                        .menuItem(new MenuItem("A2"))
                        .menuItem(new MenuItem("A3"))
                        .menuItem(new MenuBuilder()
                                .text("A...")
                                .menuItem(new MenuItem("A4"))
                                .menuItem(new MenuItem("A5"))
                                .build())
                        .build())
                .menu(new MenuBuilder()
                        .text("B")
                        .menuItem(new MenuItem("B1"))
                        .menuItem(new MenuItem("B2"))
                        .menuItem(new MenuItem("B3"))
                        .menuItem(new MenuBuilder()
                                .text("B...")
                                .menuItem(new MenuItem("B4"))
                                .menuItem(new MenuItem("B5"))
                                .build())
                        .build())
                .menu(new MenuBuilder()
                        .text("C")
                        .menuItem(new MenuItem("C1"))
                        .menuItem(new MenuItem("C2"))
                        .menuItem(new MenuItem("C3"))
                        .menuItem(new MenuBuilder()
                                .text("C...")
                                .menuItem(new MenuItem("C4"))
                                .menuItem(new MenuItem("C5"))
                                .build())
                        .build())
                .build();

        BorderPane root = new BorderPane(new StackPane(new Button("OK")), menuBar, null, null, null);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("MenuBarBuilderTest");
        primaryStage.show();
    }
}
