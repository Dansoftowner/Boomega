package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.gui.entry.EntryPoint;
import com.dansoftware.libraryapp.util.ThrowableToStringAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 * This class contains some static methods for creating alert popups for the user.
 */
@Deprecated
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
    public static void showErrorAlertDialog(EntryPoint entryPoint, Throwable cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR, cause.getMessage());
        alert.setTitle("LibraryApp alert");
        alert.getDialogPane().setExpandableContent(
                new UnEditableTextArea(new ThrowableToStringAdapter(cause).toString()));

        if (entryPoint != null) alert.initOwner(entryPoint.getPrimaryStage());

        alert.show();
    }

    private static class UnEditableTextArea extends TextArea {
        private UnEditableTextArea(String text) {
            super(text);
            this.setEditable(false);
        }
    }
}
