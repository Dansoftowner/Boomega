package com.dansoftware.boomega.main;

import com.dansoftware.boomega.gui.preloader.BackingStage;
import com.dansoftware.boomega.gui.preloader.PreloaderGUI;
import com.dansoftware.boomega.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * The preloader class for the application.
 * Uses a {@link PreloaderGUI} for building the UI easily.
 *
 * @author Daniel Gyorffy
 */
public class Preloader extends javafx.application.Preloader {

    private static final Logger logger = LoggerFactory.getLogger(Preloader.class);

    private static final String STYLESHEET = "/com/dansoftware/boomega/gui/theme/css/preloader.css";

    private BackingStage backingStage;
    private Stage contentStage;
    private final StringProperty messageProperty;

    public Preloader() {
        messageProperty = new SimpleStringProperty();
    }

    @Override
    public void start(Stage primaryStage) {
        initApplicationName();
        try {
            backingStage = new BackingStage();
            //Building the gui
            PreloaderGUI gui = PreloaderGUI.builder()
                    .messageProperty(messageProperty)
                    .build();

            Scene scene = new Scene(gui);
            scene.getStylesheets().add(STYLESHEET);
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

    private void initApplicationName() {
        // Fixes the wrong app name on the top bar issue on Gnome systems
        // See more details: https://github.com/Dansoftowner/Boomega/issues/111
        com.sun.glass.ui.Application.GetApplication().setName(System.getProperty("app.name"));
    }

    @Override
    public void stop() {
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


    /**
     * Notification for hiding the preloader-window.
     */
    public static final class HideNotification implements PreloaderNotification {
    }

    /**
     * Notification for showing the preloader-window if its hidden.
     */
    public static final class ShowNotification implements PreloaderNotification {
    }

    /**
     * Internationalized notification for sending messages to the preloader
     */
    public static class MessageNotification implements PreloaderNotification {

        private final String message;

        public MessageNotification(@NotNull String i18n, Object... args) {
            this(true, i18n, args);
        }

        public MessageNotification(@NotNull String i18n) {
            this(true, i18n);
        }

        public MessageNotification(boolean i18n, @NotNull String value, Object... args) {
            if (i18n) {
                if (ArrayUtils.isEmpty(args))
                    message = I18N.getValue(value);
                else
                    message = I18N.getValue(value, args);
            } else {
                if (ArrayUtils.isEmpty(args))
                    message = value;
                else
                    message = MessageFormat.format(value, args);
            }
        }
    }

    /**
     * Internationalized notification that will be not replaced by other messages
     */
    public static class FixedMessageNotification extends MessageNotification {

        public FixedMessageNotification(@NotNull String i18n, Object... args) {
            super(i18n, args);
        }

        public FixedMessageNotification(@NotNull String i18n) {
            super(i18n);
        }
    }

}
