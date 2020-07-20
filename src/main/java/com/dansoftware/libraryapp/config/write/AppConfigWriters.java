package com.dansoftware.libraryapp.config.write;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Factory/utility methods for creating {@link AppConfigWriter} objects.
 *
 * @author Daniel Gyorffy
 */
public class AppConfigWriters {

    private AppConfigWriters() {
    }

    /**
     * Creates an {@link AppConfigWriter} that writes the configurations to a particular file.
     *
     * <p><b>
     * <pre>
     *         File file = new File("path/to/config");
     *
     *         AppConfig appConfig = ...;
     *         try(var writer = AppConfigWriters.newAppConfigFileWriter(file)) {
     *             writer.write(appConfig);
     *         } catch (IOException e) {
     *             //handle
     *         }
     * </pre>
     *
     * @param file the file that we want to write to
     * @return the writer object.
     * @throws IOException if some I/O exception occurs
     */
    public static AppConfigWriter<IOException, IOException> newAppConfigFileWriter(File file) throws IOException {
        return new AppConfigFileWriter(file);
    }

    /**
     * Creates an {@link AppConfigWriter} that writes to the default configuration file located in
     * the <i>APPDATA</i> folder.
     *
     * @return the writer object
     * @see ApplicationDataFolder#getConfigurationFile()
     * @see #newAppConfigFileWriter(File)
     */
    public static AppConfigWriter<IOException, IOException> newAppDataFolderWriter() throws IOException {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        return newAppConfigFileWriter(applicationDataFolder.getConfigurationFile());
    }

    /**
     * Creates an {@link AppConfigWriter} that doesn't writes anything.
     *
     * @return the writer object
     */
    public static AppConfigWriter<RuntimeException, RuntimeException> newNullConfigFileWriter() {
        return new NullAppConfigWriter<>();
    }


    /**
     * Implementation of {@link AppConfigWriter} that can write the configurations to a file
     */
    private static class AppConfigFileWriter implements AppConfigWriter<IOException, IOException> {

        private final JsonWriter writer;

        public AppConfigFileWriter(File file) throws IOException {
            this.writer = new JsonWriter(new BufferedWriter(new FileWriter(file)));
        }

        @Override
        public void write(AppConfig appConfig) throws IOException {
            try {
                new Gson().toJson(appConfig.getJsonObject(), this.writer);
            } catch (JsonIOException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() throws IOException {
            this.writer.close();
        }
    }

    private static final class NullAppConfigWriter<W extends Exception, C extends Exception>
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
