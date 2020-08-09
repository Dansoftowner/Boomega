package com.dansoftware.libraryapp.locale;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * Used for accessing localized messages/values.
 */
public class I18N {

    /**
     * Don't let anyone to create an instance of this class
     */
    private I18N() {
    }

    @NotNull
    public static String getGeneralWord(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        if (ArrayUtils.isEmpty(args)) {
            return getGeneralWords().getString(key);
        }

        return MessageFormat.format(getGeneralWords().getString(key), args);
    }

    @NotNull
    public static String getAlertMsg(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        if (isEmpty(args)) {
            return getAlertMessages().getString(key);
        }

        return MessageFormat.format(getAlertMessages().getString(key), args);
    }

    public static String getProgressMessage(String key) {
        return getProgressMessages().getString(key);
    }

    @NotNull
    public static ResourceBundle getProgressMessages() throws MissingResourceException {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.ProgressMessages");
    }

    @NotNull
    public static ResourceBundle getFXMLValues() throws MissingResourceException {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.FXMLValues");
    }

    @NotNull
    public static ResourceBundle getGeneralWords() throws MissingResourceException {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.GeneralWords");
    }

    @NotNull
    public static ResourceBundle getAlertMessages() throws MissingResourceException {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.AlertMessages");
    }

}
