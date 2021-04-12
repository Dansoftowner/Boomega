package com.dansoftware.boomega.config;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * A {@link ConfigFile} represents the configuration-file that is used by this application.
 *
 * @author Daniel Gyorffy
 */
public class ConfigFile extends File {

    private static ConfigFile defaultFile;

    private final boolean nonExisted;

    public ConfigFile(String path) {
        super(path);
        nonExisted = !determineExists();
    }

    public ConfigFile(File parent, String child) {
        super(parent, child);
        nonExisted = !determineExists();
    }

    /**
     * Returns {@code true} if the file didn't exist when the {@link ConfigFile}'s constructor
     * called.
     *
     * @return {@code true} if the file didn't exist; {@code false} otherwise
     */
    public boolean isNonExisted() {
        return nonExisted;
    }

    /**
     * Determines that the file exists or not.
     * By default, it just calls the {@link File#exists()}
     * method.
     *
     * <p>
     * It can be overridden by subclasses.
     *
     * @return {@code true} if exists; {@code false} otherwise
     */
    protected boolean determineExists() {
        return exists();
    }

    /**
     * Defines a policy for creating the file on the disk
     *
     * @throws IOException if some I/O exception occurs
     */
    protected void create() throws IOException {
        File parentFile = getParentFile();
        if (parentFile != null) parentFile.mkdirs();
        createNewFile();
    }

    /**
     * Sets the default {@link ConfigFile}
     *
     * @param configFile the ConfigFile object
     */
    public static void setDefault(@NotNull ConfigFile configFile) {
        defaultFile = Objects.requireNonNull(configFile);
    }

    /**
     * Used for accessing the default {@link ConfigFile}.
     *
     * @return the default {@link ConfigFile}
     */
    public static synchronized ConfigFile getConfigFile() {
        if (defaultFile == null)
            defaultFile = new ConfigFile(System.getProperty("libraryapp.config.file.path"));
        return defaultFile;
    }
}
