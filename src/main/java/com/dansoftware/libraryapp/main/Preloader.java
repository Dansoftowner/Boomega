package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.gui.preloader.BackingStage;
import com.dansoftware.libraryapp.gui.preloader.PreloaderGUI;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Preloader extends javafx.application.Preloader {

    private static BackingStage backingStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            backingStage = new BackingStage();
            //Building the gui
            PreloaderGUI gui = PreloaderGUI.builder()
                    .parameters(getParameters().getRaw())
                    .build();

            Scene scene = new Scene(gui);
            scene.setFill(Color.TRANSPARENT);

            Stage contentStage = backingStage.createChild(StageStyle.UNDECORATED);
            contentStage.setScene(scene);
            contentStage.centerOnScreen();
            contentStage.setOnShown(event -> gui.logoAnimation());

            backingStage.show();
            contentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void stop() {
        backingStage = null;
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            backingStage.close();
            stop();
        }
    }

    public static BackingStage getBackingStage() {
        return backingStage;
    }
}
