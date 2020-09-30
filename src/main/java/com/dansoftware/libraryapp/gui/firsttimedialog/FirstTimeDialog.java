package com.dansoftware.libraryapp.gui.firsttimedialog;

import com.dansoftware.libraryapp.appdata.ConfigFile;
import com.dansoftware.libraryapp.appdata.Preferences;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link FirstTimeDialog} is used for showing a gui-dialog that should be showed only if the
 * application runs firstly on the user's computer.
 *
 * <p>
 * It provides a selection for dark/light themes, languages etc...
 *
 * <p>
 * A {@link FirstTimeDialog} is not a GUI element itself, it only launches a GUI environment.
 *
 * It uses an object for locking the thread where the dialog is showed; this lock-object can be
 * accessed through the static {@link #threadLock()} method.
 *
 * @author Daniel Gyorffy
 */
public class FirstTimeDialog {

    /**
     * The object that is used by FirstTimeDialog instances to lock the
     * thread that is used for the dialog.
     */
    private static final Object THREAD_LOCK = new Object();

    /**
     * {@code true} if a {@link FirstTimeDialog} instance is created through the {@link #createDialog()} method already.
     */
    private static boolean created;

    private FirstTimeDialog() {
    }

    public static boolean isNeeded() {
        return ConfigFile.getConfigFile().isNonExisted();
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

    public static Object threadLock() {
        return THREAD_LOCK;
    }

    /**
     * Creates a {@link FirstTimeDialog} instance.
     *
     * <p>
     * <b>This method can't be invoked in the application-lifetime more than once!</b>
     *
     * @return the instance
     */
    public static FirstTimeDialog createDialog() {
        if (created)
            throw new UnsupportedOperationException("A FirstTimeDialog can't be created more than once");
        created = true;
        return new FirstTimeDialog();
    }
}
