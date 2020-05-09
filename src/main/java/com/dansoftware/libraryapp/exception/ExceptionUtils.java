package com.dansoftware.libraryapp.exception;

import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;
import com.dansoftware.libraryapp.gui.util.Alerts;
import com.dansoftware.libraryapp.main.Main;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import static com.dansoftware.libraryapp.locale.Bundles.getGeneralWords;

/**
 * This class contains some utilities for exception handling
 */
public class ExceptionUtils {

    private ExceptionUtils() {
    }

    /**
     * This exception handler responsible for handling runtime exceptions.
     * If a runtime exception occurs, this handler will show that on the gui
     * for the user.
     */
    public static final Thread.UncaughtExceptionHandler DEFAULT_EXCEPTION_HANDLER = (var thread, var exception) ->
            Notification.create()
                    .level(NotificationLevel.ERROR)
                    .title("error.occurred")
                    .msg("error.details")
                    .cause(exception)
                    .show();

}
