package com.dansoftware.boomega.gui.firsttime;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.gui.firsttime.dialog.FirstTimeDialogActivity;
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

    public void show() {
        showFirstTimeDialog(preferences);
    }

    private void showFirstTimeDialog(@NotNull Preferences preferences) {
        new FirstTimeDialogActivity(preferences).show();
    }

    public static boolean isNeeded(@NotNull Preferences preferences) {
        return preferences.getSource().isCreated();
    }
}
