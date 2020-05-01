package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.gui.preloader.PreloaderGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.tools.ToolProvider;

public class Preloader extends javafx.application.Preloader {

    private static Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            preloaderStage = primaryStage;

            PreloaderGUI gui = new PreloaderGUI();

            Scene scene = new Scene(gui);
            scene.setFill(Color.TRANSPARENT);

            preloaderStage.setScene(scene);
            preloaderStage.setOnCloseRequest(WindowEvent::consume);
            preloaderStage.setTitle("Starting LibraryApp");
            preloaderStage.getIcons().add(Globals.WINDOW_ICON);
            preloaderStage.initStyle(StageStyle.TRANSPARENT);
            preloaderStage.centerOnScreen();
            preloaderStage.setOnShown(event ->
                    new animatefx.animation
                            .BounceIn(gui.getCenter())
                            .play());

            preloaderStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void stop() {
        preloaderStage = null;
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
            stop();
        }
    }

    public static Stage getPreloaderStage() {
        return preloaderStage;
    }
}
