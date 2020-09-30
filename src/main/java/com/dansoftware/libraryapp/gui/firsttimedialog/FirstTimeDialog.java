package com.dansoftware.libraryapp.gui.firsttimedialog;

import com.dansoftware.libraryapp.appdata.Preferences;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class FirstTimeDialog {

    /**
     * The object that is used by FirstTimeDialog instances to lock the
     * thread that is used for the dialog.
     */
    public static final Object THREAD_LOCK = FirstTimeDialog.class;

    /**
     * {@code true} if a {@link FirstTimeDialog} instance is created through the {@link #getDialog()} method already.
     */
    private static boolean created;

    private FirstTimeDialog() {
    }

    public static boolean isNeeded() {
        return false;
    }

    public void show(@NotNull Preferences preferences) {
        Platform.runLater(() -> {
           synchronized (THREAD_LOCK) {
               //showing it
               //...
               THREAD_LOCK.notify();
           }
        });
    }

    /**
     * Creates a {@link FirstTimeDialog} instance.
     *
     * <p>
     * <b>This method can't be invoked in the application-lifetime more than once!</b>
     *
     * @return the instance
     */
    public static FirstTimeDialog getDialog() {
        if (created)
            throw new UnsupportedOperationException("A FirstTimeDialog can't be created more than once");
        created = true;
        return new FirstTimeDialog();
    }
}
