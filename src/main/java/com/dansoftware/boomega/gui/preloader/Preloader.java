/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.preloader;

import com.dansoftware.boomega.gui.font.CustomFontsLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The preloader class for the application.
 * Uses a {@link PreloaderGUI} for building the UI easily.
 *
 * @author Daniel Gyorffy
 */
public class Preloader extends javafx.application.Preloader {

    private static final Logger logger = LoggerFactory.getLogger(Preloader.class);
    private static final String STYLESHEET = "/com/dansoftware/boomega/gui/theme/preloader.css";

    static {
        CustomFontsLoader.loadFonts();
    }

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
            var gui = new PreloaderView();

            Scene scene = new Scene(gui);
            scene.getStylesheets().add(STYLESHEET);
            scene.setFill(Color.TRANSPARENT);

            contentStage = backingStage.createChild(StageStyle.UNDECORATED);
            contentStage.setScene(scene);
            contentStage.centerOnScreen();

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
        if (!messageProperty.isBound() && info instanceof MessageNotification msgNotification)
            if (msgNotification.priority == MessageNotification.Priority.HIGH)
                messageProperty.bind(new SimpleStringProperty(msgNotification.message));
            else messageProperty.set(msgNotification.message);
        else if (info instanceof HideNotification) contentStage.hide();
        else if (info instanceof ShowNotification) contentStage.show();
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
     * Notification for sending messages to the preloader
     */
    public static class MessageNotification implements PreloaderNotification {

        protected final Priority priority;
        protected final String message;

        public MessageNotification(@NotNull String message) {
            this(message, Priority.REGULAR);
        }

        public MessageNotification(@NotNull String message, @NotNull Priority priority) {
            this.priority = priority;
            this.message = message;
        }

        public enum Priority {
            HIGH,
            REGULAR
        }
    }

}
