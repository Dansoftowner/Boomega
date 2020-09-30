package com.dansoftware.libraryapp.gui.firsttimedialog;

import com.dansoftware.libraryapp.appdata.Preferences;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

public class FirstTimeDialog {

    public FirstTimeDialog() {
    }

    public static boolean isNeeded() {
        return false;
    }

    public void show(@NotNull Preferences preferences) {
        Platform.runLater(() -> {
           synchronized (FirstTimeDialog.class) {
               //showing it
               //...
               FirstTimeDialog.class.notify();
           }
        });
    }
}
