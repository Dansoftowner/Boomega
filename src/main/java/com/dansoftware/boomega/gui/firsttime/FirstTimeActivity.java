package com.dansoftware.boomega.gui.firsttime;

import com.dansoftware.boomega.appdata.ConfigFile;
import com.dansoftware.boomega.appdata.Preferences;
import com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialog;
import com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialogActivity;
import com.dansoftware.boomega.gui.firsttime.imp.ConfigurationImportActivity;
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
 * <p>
 * Basically, it shows a {@link ConfigurationImportActivity} and then a {@link FirstTimeDialogActivity}.
 *
 * @author Daniel Gyorffy
 */
public class FirstTimeActivity {

    private final Preferences preferences;

    /**
     * Creates a simple {@link FirstTimeActivity}.
     * <p>
     *
     * @param preferences the {@link Preferences} object that the {@link FirstTimeActivity}
     *                    should read to
     */
    public FirstTimeActivity(@NotNull Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "Preferences shouldn't be null");
    }

    /**
     * Shows the {@link ConfigurationImportActivity} and the {@link FirstTimeDialog} if needed.
     */
    public void show() {
        if (!showConfigurationImport(preferences))
            showFirstTimeDialog(preferences);
    }

    private boolean showConfigurationImport(@NotNull Preferences preferences) {
        return new ConfigurationImportActivity(preferences).show();
    }

    private void showFirstTimeDialog(@NotNull Preferences preferences) {
        new FirstTimeDialogActivity(preferences).show();
    }

    public static boolean isNeeded() {
        return ConfigFile.getConfigFile().isNonExisted();
    }
}
