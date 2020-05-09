package com.dansoftware.libraryapp.locale;

import java.util.ResourceBundle;

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
