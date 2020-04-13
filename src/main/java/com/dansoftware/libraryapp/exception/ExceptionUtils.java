package com.dansoftware.libraryapp.exception;

import com.dansoftware.libraryapp.main.Main;
import com.dansoftware.libraryapp.util.Alerts;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import static com.dansoftware.libraryapp.util.Bundles.*;

/**
 * This class contains some utilities for exception handling
 */
public class ExceptionUtils {

    /**
     * Don't let anyone to create an instance of this class
     */
    private ExceptionUtils() {
    }

    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (var thread, var exception) -> {
        /*if (exception instanceof UnsupportedOperationException) {
            exception.printStackTrace();
            return;
        }*/

        Main.runAfterStart(() -> Notifications.create()
                .title(exception.getMessage())
                .text(getCommonBundle().getString("notifications.clickfordetails"))
                .hideAfter(Duration.INDEFINITE)
                .position(Pos.BOTTOM_RIGHT)
                .owner(Main.getPrimaryStage())
                .onAction(event -> Alerts.showErrorAlertDialog(exception))
                .showError());
    };



    public static Thread.UncaughtExceptionHandler getExceptionHandler() {
        return EXCEPTION_HANDLER;
    }

}
