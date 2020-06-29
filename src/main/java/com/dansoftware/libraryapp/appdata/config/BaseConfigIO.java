package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import java.io.*;

/**
 * The default configuration reader/writer of the application.
 * Reads from and writes to the configuration file located in the application-data
 * folder.
 *
 * @see ApplicationDataFolder
 * @see ApplicationDataFolder#getConfigurationFile()
 */
public class BaseConfigIO implements ConfigIO {
    @Override
    public AppConfig read() throws IOException {

        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File configurationFile = applicationDataFolder.getConfigurationFile();

        try(var reader = new BufferedReader(new FileReader(configurationFile))) {
            return new AppConfig(new Gson().fromJson(reader, JsonObject.class));
        } catch (JsonIOException | JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(AppConfig appConfig) throws IOException {

        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        File configurationFile = applicationDataFolder.getConfigurationFile();

        try(var writer = new JsonWriter(new BufferedWriter(new FileWriter(configurationFile)))) {
            new Gson().toJson(appConfig.getJsonObject(), writer);
        }
    }
}
