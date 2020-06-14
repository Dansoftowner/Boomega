package com.dansoftware.libraryapp.main.init.step.impl;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.config.ConfigurationBase;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.main.Globals;
import com.dansoftware.libraryapp.main.init.step.Step;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static java.util.Objects.isNull;

public class ConfigurationReader implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationReader.class);

    @Override
    public void call() {
        ApplicationDataFolder appDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File configFile = appDataFolder.getConfigurationFile();

        Gson gson = new Gson();
        try (var reader = new FileReader(configFile)) {
            ConfigurationBase configurationBase = gson.fromJson(reader, ConfigurationBase.class);
            configurationBase = isNull(configurationBase) ? ConfigurationBase.EMPTY : configurationBase;
            ConfigurationBase.setGlobal(configurationBase);
            this.setDefaultValues(configurationBase);

            LOGGER.debug("Read configurations from {}", configFile);
        } catch (IOException e) {
            LOGGER.error("Couldn't read configurations", e);
        }
    }

    private void setDefaultValues(ConfigurationBase configurationBase) {
        var locale = configurationBase.getLocale();
        if (locale != null) Locale.setDefault(locale);

        LOGGER.debug("Default locale: {}", Locale.getDefault());

        var theme = configurationBase.getTheme();
        if (theme != null)
            Theme.setDefault(configurationBase.getTheme());
    }
}
