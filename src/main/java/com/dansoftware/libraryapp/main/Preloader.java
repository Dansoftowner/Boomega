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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import javax.tools.ToolProvider;
import java.io.File;
import java.util.List;

public class Preloader extends javafx.application.Preloader {

    private static Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            preloaderStage = primaryStage;
            preloaderStage.initStyle(StageStyle.UTILITY);
            preloaderStage.setOnCloseRequest(WindowEvent::consume);
            preloaderStage.setTitle("Starting LibraryApp");
            preloaderStage.setOpacity(0);

            //Building the gui
            PreloaderGUI gui = PreloaderGUI.builder()
                    .parameters(getParameters().getRaw())
                    .build();

            Scene scene = new Scene(gui);
            scene.setFill(Color.TRANSPARENT);

            Stage contentStage = new Stage(StageStyle.UNDECORATED);
            contentStage.initOwner(preloaderStage);
            contentStage.setScene(scene);
            contentStage.centerOnScreen();
            contentStage.setOnShown(event -> gui.logoAnimation());

            preloaderStage.show();
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
