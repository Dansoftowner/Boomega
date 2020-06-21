package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.gui.theme.Theme;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class StyledAlert extends Alert {

    /* init */ {
        //styling
        var dialogPane = this.getDialogPane();
        Theme.applyDefault(dialogPane);
    }

    public StyledAlert(AlertType alertType) {
        super(alertType);
    }

    public StyledAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
    }
}
