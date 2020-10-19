package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.gui.preloader.BackingStage;
import com.dansoftware.libraryapp.gui.preloader.PreloaderGUI;
import com.dansoftware.libraryapp.locale.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Preloader extends javafx.application.Preloader {

    private static final Logger logger = LoggerFactory.getLogger(Preloader.class);

    private static BackingStage backingStage;

    private Stage contentStage;
    private final StringProperty messageProperty;

    public Preloader() {
        messageProperty = new SimpleStringProperty();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            backingStage = new BackingStage();
            //Building the gui
            PreloaderGUI gui = PreloaderGUI.builder()
                    .messageProperty(messageProperty)
                    .build();

            Scene scene = new Scene(gui);
            scene.setFill(Color.TRANSPARENT);

            contentStage = backingStage.createChild(StageStyle.UNDECORATED);
            contentStage.setScene(scene);
            contentStage.centerOnScreen();
            contentStage.setOnShown(event -> gui.logoAnimation());

            backingStage.show();
            contentStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (!messageProperty.isBound() && info instanceof FixedMessageNotification) {
            MessageNotification messageNotification = (MessageNotification) info;
            messageProperty.bind(new SimpleStringProperty(messageNotification.message));
        } else if (!messageProperty.isBound() && info instanceof MessageNotification) {
            MessageNotification messageNotification = (MessageNotification) info;
            messageProperty.set(messageNotification.message);
        } else if (info instanceof HideNotification) {
            contentStage.hide();
        } else if (info instanceof ShowNotification) {
            contentStage.show();
        }
    }

    public static BackingStage getBackingStage() {
        return backingStage;
    }

    public static class HideNotification implements PreloaderNotification {
    }

    public static class ShowNotification implements PreloaderNotification {
    }

    public static class MessageNotification implements PreloaderNotification {

        private final String message;

        public MessageNotification(@NotNull String i18n, Object... args) {
            this.message = I18N.getProgressMessage(i18n, args);
        }

        public MessageNotification(@NotNull String i18n) {
            this.message = I18N.getProgressMessage(i18n);
        }
    }

    public static class FixedMessageNotification extends MessageNotification {

        public FixedMessageNotification(@NotNull String i18n, Object... args) {
            super(i18n, args);
        }

        public FixedMessageNotification(@NotNull String i18n) {
            super(i18n);
        }
    }

}
