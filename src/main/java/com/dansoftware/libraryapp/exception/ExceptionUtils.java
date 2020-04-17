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

    /**
     * This exception handler responsible for handling runtime exceptions.
     * If a runtime exception occurs, this handler will show that on the gui
     * for the user.
     */
    public static final Thread.UncaughtExceptionHandler DEFAULT_EXCEPTION_HANDLER = (var thread, var exception) -> {
        Main.runAfterStart(() -> Notifications.create()
                .title(getCommonBundle().getString("notifications.error.occured"))
                .text(getCommonBundle().getString("notifications.clickfordetails"))
                .hideAfter(Duration.INDEFINITE)
                .position(Pos.BOTTOM_RIGHT)
                .owner(Main.getPrimaryStage())
                .onAction(event -> Alerts.showErrorAlertDialog(exception))
                .showError());
    };

}
