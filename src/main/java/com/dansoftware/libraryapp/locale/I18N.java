package com.dansoftware.libraryapp.locale;

import javafx.fxml.FXML;
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

    private static final String WINDOW_TITLES = "com.dansoftware.libraryapp.locale.WindowTitles";
    private static final String FIRST_TIME_DIALOG = "com.dansoftware.libraryapp.locale.FirstTimeDialog";
    private static final String PROGRESS_MESSAGES = "com.dansoftware.libraryapp.locale.ProgressMessages";
    private static final String FXML_VALUES = "com.dansoftware.libraryapp.locale.FXMLValues";
    private static final String GENERAL_WORDS = "com.dansoftware.libraryapp.locale.GeneralWords";
    private static final String ALERT_MESSAGES = "com.dansoftware.libraryapp.locale.AlertMessages";

    static {
        try {
            //just for executing the FXI18N's static block
            Class.forName(FXI18N.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Don't let anyone to create an instance of this class
     */
    private I18N() {
    }

    @NotNull
    public static String getGeneralWord(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        if (ArrayUtils.isEmpty(args)) return getGeneralWords().getString(key);
        return getFormat(getGeneralWords(), key, args);
    }

    @NotNull
    public static String getAlertMsg(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        if (isEmpty(args)) return getAlertMessages().getString(key);
        return getFormat(getAlertMessages(), key, args);
    }

    @NotNull
    public static String getProgressMessage(String key, Object... args) {
        if (ArrayUtils.isEmpty(args)) return getProgressMessages().getString(key);
        return getFormat(getProgressMessages(), key, args);
    }

    public static ResourceBundle getWindowTitles() throws MissingResourceException {
        return ResourceBundle.getBundle(WINDOW_TITLES);
    }

    @NotNull
    public static ResourceBundle getFirstTimeDialogValues() {
        return ResourceBundle.getBundle(FIRST_TIME_DIALOG);
    }

    @NotNull
    public static ResourceBundle getProgressMessages() throws MissingResourceException {
        return ResourceBundle.getBundle(PROGRESS_MESSAGES);
    }

    @NotNull
    public static ResourceBundle getFXMLValues() throws MissingResourceException {
        return ResourceBundle.getBundle(FXML_VALUES);
    }

    @NotNull
    public static ResourceBundle getGeneralWords() throws MissingResourceException {
        return ResourceBundle.getBundle(GENERAL_WORDS);
    }

    @NotNull
    public static ResourceBundle getAlertMessages() throws MissingResourceException {
        return ResourceBundle.getBundle(ALERT_MESSAGES);
    }

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

}
