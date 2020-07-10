package com.dansoftware.libraryapp.locale;

import java.text.MessageFormat;
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

    public static String getGeneralWord(String key) {
        return getGeneralWords().getString(key);
    }

    public static String getAlertMsg(String key, Object... args) {
        if (isEmpty(args)) {
            return getAlertMessages().getString(key);
        }

        return MessageFormat.format(getAlertMessages().getString(key), args);
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

    public static ResourceBundle getAlertMessages() {
        return ResourceBundle.getBundle("com.dansoftware.libraryapp.locale.AlertMessages");
    }

}
