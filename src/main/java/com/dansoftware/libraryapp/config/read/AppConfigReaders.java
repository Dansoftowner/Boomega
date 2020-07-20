package com.dansoftware.libraryapp.config.read;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.Objects;

/**
 * Factory/utility methods for creating {@link AppConfigReader} objects.
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
     * <p><br>
     * <pre>
     *     File file = new File("path/to/config");
     *
     *     AppConfig appConfig = null;
     *     try(var reader = AppConfigReaders.newAppConfigFileReader(file)) {
     *         appConfig = reader.read();
     *     } catch (IOException e) {
     *         //handle
     *     }
     *
     *     //using appConfig...
     * </pre>
     *
     * @param file the file that we want to read from
     * @return the reader object.
     * @throws IOException if some I/O exception occurs
     */
    public static AppConfigReader<IOException, IOException> newAppConfigFileReader(File file) {
        return new AppConfigFileReader(file);
    }

    /**
     * Creates an {@link AppConfigReader} that reads from the default configuration file located in
     * the <i>APPDATA</i> folder.
     *
     * @return the reader object
     * @see ApplicationDataFolder#getConfigurationFile()
     * @see #newAppConfigFileReader(File)
     */
    public static AppConfigReader<IOException, IOException> newAppDataFolderReader() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        return newAppConfigFileReader(applicationDataFolder.getConfigurationFile());
    }

    /**
     * Creates an {@link AppConfigReader} that doesn't reads anything.
     *
     * @return the reader object
     */
    public static AppConfigReader<RuntimeException, RuntimeException> newNullAppConfigReader() {
        return new NullAppConfigReader<>();
    }

    /**
     * Implementation of {@link AppConfigReader} that can read the configurations from file.
     */
    private static class AppConfigFileReader implements AppConfigReader<IOException, IOException> {

        private File file;
        private Reader reader;

        private AppConfigFileReader(File file) {
            this.file = file;
        }

        @Override
        public AppConfig read() throws IOException {
            this.reader = new BufferedReader(new FileReader(file));

            try {
                JsonObject read = new Gson().fromJson(reader, JsonObject.class);
                return new AppConfig(Objects.isNull(read) ? new JsonObject() : read);
            } catch (JsonIOException | JsonSyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() throws IOException {
            if (Objects.nonNull(this.reader)) {
                this.reader.close();
            }
        }
    }

    private static class NullAppConfigReader<R extends Exception, C extends Exception> implements AppConfigReader<R, C> {

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
