package com.dansoftware.libraryapp.gui.firsttime;

import com.dansoftware.libraryapp.appdata.ConfigFile;
import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialog;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialogActivity;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialogWindow;
import com.dansoftware.libraryapp.gui.firsttime.imp.ConfigurationImportActivity;
import com.dansoftware.libraryapp.main.Preloader;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A {@link FirstTimeActivity} is used for showing a gui-dialog that should be showed only if the
 * application runs firstly on the user's computer.
 *
 * <p>
 * It provides a selection for dark/light themes, languages etc...
 *
 * <p>
 * A {@link FirstTimeActivity} is not a GUI element itself, it only launches a GUI environment.
 *
 * @author Daniel Gyorffy
 */
public class FirstTimeActivity {

    /**
     * The object that is used by FirstTimeDialog instances to lock the
     * thread that is used for the dialog.
     */
    private final Object threadLock;

    private final Preferences preferences;

    /**
     * Creates a simple {@link FirstTimeActivity}.
     *
     * @param threadLock  the object that will the FirstTimeActivity synchronize on.
     * @param preferences the {@link Preferences} object that the {@link FirstTimeActivity}
     *                    should read to
     */
    public FirstTimeActivity(@NotNull Object threadLock,
                             @NotNull Preferences preferences) {
        this.threadLock = Objects.requireNonNull(threadLock, "threadLock shouldn't be null");
        this.preferences = Objects.requireNonNull(preferences, "Preferences shouldn't be null");
    }

    /**
     * Shows the {@link ConfigurationImportActivity} and the {@link FirstTimeDialog} if needed.
     */
    public void show() {
        Platform.runLater(() -> {
            synchronized (threadLock) {
                if (!showConfigurationImport(preferences))
                    showFirstTimeDialog(preferences);
                threadLock.notify();
            }
        });
    }

    private boolean showConfigurationImport(@NotNull Preferences preferences) {
        return new ConfigurationImportActivity(preferences).show(Preloader.getBackingStage());
    }

    private void showFirstTimeDialog(@NotNull Preferences preferences) {
        new FirstTimeDialogActivity(preferences).show();
    }

    public static boolean isNeeded() {
        return ConfigFile.getConfigFile().isNonExisted();
    }
}
