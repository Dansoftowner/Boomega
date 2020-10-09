package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.main.Preloader;
import org.jetbrains.annotations.NotNull;

public class ConfigurationImportActivity {

    public ConfigurationImportActivity() {
    }

    public boolean show(@NotNull Preferences preferences) {
        try {
            ConfigurationImportView configurationImportView = new ConfigurationImportView(preferences);
            ConfigurationImportWindow configurationImportWindow = Preloader.getBackingStage()
                    .createChild(ConfigurationImportWindow.class, configurationImportView);
            configurationImportWindow.showAndWait();
            return configurationImportView.getController().isImported();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
