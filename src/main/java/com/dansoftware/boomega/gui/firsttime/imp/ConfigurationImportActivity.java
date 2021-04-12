package com.dansoftware.boomega.gui.firsttime.imp;

import com.dansoftware.boomega.config.Preferences;
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
     * @return {@code true} if the user imported settings; {@code false} otherwise.
     */
    public boolean show() {
        ConfigurationImportView configurationImportView = new ConfigurationImportView(preferences);
        ConfigurationImportWindow configurationImportWindow = new ConfigurationImportWindow(configurationImportView);
        configurationImportWindow.showAndWait();
        return configurationImportView.externalSettingsImported();
    }
}
