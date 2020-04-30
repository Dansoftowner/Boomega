package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.util.ThrowableToStringAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import static com.dansoftware.libraryapp.main.GuiApplicationStarter.getPrimaryStage;

/**
 * This class contains some static methods for creating alert popups for the user.
 */
public final class Alerts {

    /**
     * Don't let anyone to create an instance of this class
     */
    private Alerts() {
    }

    /**
     * This method creates an alert window for the user that can show the throwable's
     * stack-trace on the gui
     *
     * @param cause the Throwable object.
     */
    public static void showErrorAlertDialog(Throwable cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR, cause.getMessage());
        alert.setTitle("LibraryApp alert");
        alert.initOwner(getPrimaryStage());
        alert.getDialogPane().setExpandableContent(
                new TextArea(new ThrowableToStringAdapter(cause).toString()));
        alert.show();
    }

}
