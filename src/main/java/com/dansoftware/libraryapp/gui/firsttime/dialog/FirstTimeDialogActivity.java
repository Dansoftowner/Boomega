package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.appdata.Preferences;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FirstTimeDialogActivity {

    private final Preferences preferences;

    public FirstTimeDialogActivity(@NotNull Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "Preferences shouldn't be null");
    }

    public void show() {
        FirstTimeDialogWindow window = new FirstTimeDialogWindow(new FirstTimeDialog(preferences));
        window.showAndWait();
    }

}
