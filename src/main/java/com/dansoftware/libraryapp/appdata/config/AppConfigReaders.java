package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;

/**
 * Factory/utility methods for {@link AppConfigReader}.
 *
 * @author Daniel Gyorffy
 */
public final class AppConfigReaders {

    /**
     * Can't instantiate.
     */
    private AppConfigReaders() {
    }

    /**
     * Creates an {@link AppConfigReader} that reads the configurations from a particular file.
     *
     * @param file the file that we want to read from
     * @return the reader object.
     * @throws IOException if some I/O exception occurs
     */
    public static AppConfigReader<IOException, IOException> newAppConfigFileReader(File file) throws IOException {
        return new AppConfigFileReader(file);
    }

    /**
     * Creates an {@link AppConfigReader} that reads from the default configuration file located in
     * the <i>APPDATA</i> folder.
     *
     * @return the reader object
     */
    public static AppConfigReader<IOException, IOException> newAppDataFolderReader() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        try {
            return newAppConfigFileReader(applicationDataFolder.getConfigurationFile());
        } catch (IOException e) {
            return new NullAppConfigReader<>();
        }
    }

    private static class AppConfigFileReader implements AppConfigReader<IOException, IOException> {

        private final Reader reader;

        private AppConfigFileReader(File file) throws IOException {
            this.reader = new BufferedReader(new FileReader(file));
        }

        @Override
        public AppConfig read() throws IOException {
            try {
                return new AppConfig(new Gson().fromJson(reader, JsonObject.class));
            } catch (JsonIOException | JsonSyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }

    private static class NullAppConfigReader<R extends Throwable, C extends Exception> implements AppConfigReader<R, C> {

        @Override
        public AppConfig read() throws R {
            return new AppConfig();
        }

        @Override
        public void close() throws C {
            //do nothing
        }
    }

}
