package com.dansoftware.libraryapp.locale;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * This class is used for accessing the necessary
 * Resource Bundles.
 */
public class Bundles {

    /**
     * Don't let anyone to create an instance of this class
     */
    private Bundles() {
    }

    public static String getGeneralWord(String key) {
        return getGeneralWords().getString(key);
    }

    public static String getNotificationMsg(String key, Object... args) {
        if (isEmpty(args)) {
            return getNotificationMessages().getString(key);
        }

        return MessageFormat.format(getNotificationMessages().getString(key), args);
    }

    public static ResourceBundle getProgressMessages() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.ProgressMessages");
    }

    public static ResourceBundle getFXMLValues() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.FXMLValues");
    }

    public static ResourceBundle getGeneralWords() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.GeneralWords");
    }

    public static ResourceBundle getNotificationMessages() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.NotificationMessages");
    }

}
