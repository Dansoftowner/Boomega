package com.dansoftware.libraryapp.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import static com.dansoftware.libraryapp.main.Main.getPrimaryStage;

public final class Alerts {

    /**
     * Don't let anyone to create an instance of this class
     */
    private Alerts() {
    }

    public static void showErrorAlertDialog(Throwable cause) {
        Alert alert = new Alert(Alert.AlertType.ERROR, cause.getMessage());
        alert.setTitle("Application error");
        alert.initOwner(getPrimaryStage());
        alert.getDialogPane().setExpandableContent(
                new TextArea(new ThrowableToStringAdapter(cause).toString()));
        alert.show();
    }

}
