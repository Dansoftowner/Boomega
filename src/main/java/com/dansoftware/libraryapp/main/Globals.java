package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.config.ConfigurationHolder;
import com.dansoftware.libraryapp.db.DataStorage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.nonNull;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
public final class Globals {

    public static final Image WINDOW_ICON;
    static {
        Image temp = null;
        try(InputStream inputStream =
                    Globals.class.getResourceAsStream("/com/dansoftware/libraryapp/image/libraryapp-win-icon.png")) {
            temp = new Image(inputStream);
        } catch (IOException ignored) {
            //we don't care about that the input stream closed with throwing exception
        }

        WINDOW_ICON = temp;
    }

    public static final String APP_VERSION = "0.0.0";
    public static final String BUILD_INFO = "";

    private static ConfigurationHolder configurationHolder;
    private static DataStorage dataStorage;

    static void setDataStorage(DataStorage dataStorage) {
        if (nonNull(dataStorage))
            throw new UnsupportedOperationException("The default dataStorage" +
                    " cannot be initialized more than once.");
    }

    /**
     * Sets the default ConfigurationHolder.
     *
     * <p>
     * Should be used by the {@link ApplicationInitializer}.
     * <b>Should be called only ONCE</b>
     *
     * @param holder the new configuration holder
     * @throws UnsupportedOperationException if the default configuration holder already instantiated
     */
    static void setConfigurationHolder(ConfigurationHolder holder) {
        if (nonNull(configurationHolder))
            throw new UnsupportedOperationException("The default configurationHolder" +
                    " cannot be initialized more than once.");

        configurationHolder = holder;
    }

    public static DataStorage getDataStorage() {
        return dataStorage;
    }

    /**
     * Gives access to the default ConfigurationHolder
     * @return the default configuration holder
     */
    public static ConfigurationHolder getConfigurationHolder() {
        return configurationHolder;
    }

    /**
     * Don't let anyone to create an instance of this class
     */
    private Globals() {
    }

}
