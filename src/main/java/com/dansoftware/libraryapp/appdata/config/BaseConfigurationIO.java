package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.dizitart.no2.Nitrite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static java.util.Objects.isNull;

public class BaseConfigurationIO implements ConfigurationIO {

    private File getConfigurationFile() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        return applicationDataFolder.getConfigurationFile();
    }

    @Override
    public ConfigurationBase read() throws IOException {
        Gson gson = new Gson();
        try (var reader = new FileReader(getConfigurationFile())) {
            ConfigurationBase configurationBase = gson.fromJson(reader, ConfigurationBase.class);
            return isNull(configurationBase) ? ConfigurationBase.EMPTY : configurationBase;
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(ConfigurationBase configurationBase) {

    }
}
