package com.dansoftware.libraryapp.gui.firsttime;

import com.dansoftware.libraryapp.appdata.ConfigFile;
import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialog;
import com.dansoftware.libraryapp.gui.firsttime.dialog.FirstTimeDialogWindow;
import com.dansoftware.libraryapp.gui.firsttime.imp.ConfigurationImportView;
import com.dansoftware.libraryapp.gui.firsttime.imp.ConfigurationImportWindow;
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
 * It uses an object for locking the thread where the dialog is showed; this lock-object can be
 * accessed through the static {@link #threadLock()} method.
 *
 * @author Daniel Gyorffy
 */
public class FirstTimeActivity {

    /**
     * The object that is used by FirstTimeDialog instances to lock the
     * thread that is used for the dialog.
     */
    private final Object threadLock;

    public FirstTimeActivity(@NotNull Object threadLock) {
        this.threadLock = Objects.requireNonNull(threadLock, "threadLock shouldn't be null");
    }

    public void show(@NotNull Preferences preferences) {
        Platform.runLater(() -> {
            synchronized (threadLock) {
                if (!showConfigurationImport(preferences))
                    showFirstTimeDialog(preferences);
                threadLock.notify();
            }
        });
    }

    private boolean showConfigurationImport(Preferences preferences) {
        var configurationImportWindow = new ConfigurationImportWindow(new ConfigurationImportView(preferences));
        configurationImportWindow.showAndWait();
        return true;
    }

    private void showFirstTimeDialog(Preferences preferences) {
        FirstTimeDialogWindow window = new FirstTimeDialogWindow(new FirstTimeDialog());
        window.showAndWait();
    }

    public static boolean isNeeded() {
        return ConfigFile.getConfigFile().isNonExisted();
    }
}
