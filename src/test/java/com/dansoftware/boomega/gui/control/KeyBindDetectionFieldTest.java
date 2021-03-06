package com.dansoftware.boomega.gui.control;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KeyBindDetectionFieldTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        var field = new KeyBindDetectionField(new KeyCodeCombination(KeyCode.K, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        var field2 = new KeyBindDetectionField(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));

        primaryStage.setScene(new Scene(new StackPane(new Group(new VBox(field, field2)))));
        primaryStage.show();
    }
}
