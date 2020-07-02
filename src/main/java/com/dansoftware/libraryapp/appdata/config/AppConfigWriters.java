package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AppConfigWriters {

    private AppConfigWriters() {
    }

    public static AppConfigWriter<IOException, IOException> newAppConfigFileWriter(File file) throws IOException {
        return new AppConfigFileWriter(file);
    }

    public static AppConfigWriter<IOException, IOException> newAppDataFolderWriter() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        try {
            return newAppConfigFileWriter(applicationDataFolder.getConfigurationFile());
        } catch (IOException e) {
            return new NullAppConfigWriter<>();
        }
    }

    private static class AppConfigFileWriter implements AppConfigWriter<IOException, IOException> {

        private final JsonWriter writer;

        public AppConfigFileWriter(File file) throws IOException {
            this.writer = new JsonWriter(new BufferedWriter(new FileWriter(file)));
        }

        @Override
        public void write(AppConfig appConfig) throws IOException {
            new Gson().toJson(appConfig.getJsonObject(), this.writer);
        }

        @Override
        public void close() throws IOException {
            this.writer.close();
        }
    }

    private static final class NullAppConfigWriter<W extends Throwable, C extends Exception>
            implements AppConfigWriter<W, C> {

        @Override
        public void write(AppConfig appConfig) throws W {
            //do nothing
        }

        @Override
        public void close() throws C {
            //do nothing
        }
    }
}
