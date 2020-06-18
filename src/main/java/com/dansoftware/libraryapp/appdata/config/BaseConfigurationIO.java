package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    public void write(ConfigurationBase configurationBase) throws IOException {
        Gson gson = new Gson();
        try(var writer = new FileWriter(getConfigurationFile())) {
            String json = gson.toJson(configurationBase);
            writer.write(json);
        }
    }
}
