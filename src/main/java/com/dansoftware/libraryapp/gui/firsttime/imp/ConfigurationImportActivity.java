package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.preloader.BackingStage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A {@link ConfigurationImportActivity} can show a {@link ConfigurationImportView}
 * with a {@link ConfigurationImportWindow}. It's used, when the application runs first
 * and the user should have a dialog that allows to import previous configurations.
 *
 * @author Daniel Gyorffy
 */
public class ConfigurationImportActivity {

    private final Preferences preferences;

    public ConfigurationImportActivity(@NotNull Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences);
    }

    /**
     * Shows the activity and waits until it's closed.
     *
     * @param backingStage the backing stage that the {@link ConfigurationImportWindow} should
     *                     show on
     * @return {@code true} if the user imported settings; {@code false} otherwise.
     */
    public boolean show(@NotNull BackingStage backingStage) {
        Objects.requireNonNull(backingStage);
        try {
            ConfigurationImportView configurationImportView = new ConfigurationImportView(preferences);
            ConfigurationImportWindow configurationImportWindow =
                    backingStage.createChild(ConfigurationImportWindow.class, configurationImportView);
            configurationImportWindow.showAndWait();
            return configurationImportView.externalSettingsImported();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
