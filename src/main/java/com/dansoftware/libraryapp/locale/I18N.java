package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.*;
import java.util.spi.LocaleServiceProvider;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * Used for accessing localized messages/values.
 */
public class I18N {

    private static final List<Locale> availableLocales;

    private static final String WINDOW_TITLES = "com.dansoftware.libraryapp.locale.WindowTitles";
    private static final String FIRST_TIME_DIALOG = "com.dansoftware.libraryapp.locale.FirstTimeDialog";
    private static final String PROGRESS_MESSAGES = "com.dansoftware.libraryapp.locale.ProgressMessages";
    private static final String FXML_VALUES = "com.dansoftware.libraryapp.locale.FXMLValues";
    private static final String GENERAL_WORDS = "com.dansoftware.libraryapp.locale.GeneralWords";
    private static final String ALERT_MESSAGES = "com.dansoftware.libraryapp.locale.AlertMessages";

    static {
        ReflectionUtils.invokeStaticBlock(FXI18N.class);
        availableLocales = calcAvailableLocales();
    }

    private static ClassLoader getBundleLoader() {
        return PluginClassLoader.getInstance();
    }

    private static List<Locale> calcAvailableLocales() {
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(locale -> !locale.equals(Locale.ROOT))
                .collect(Collectors.toList());
    }

    public static List<Locale> getAvailableLocales() {
        return availableLocales;
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
        return ResourceBundle.getBundle(WINDOW_TITLES, Locale.getDefault(), getBundleLoader());
    }

    @NotNull
    public static ResourceBundle getFirstTimeDialogValues() {
        return ResourceBundle.getBundle(FIRST_TIME_DIALOG, Locale.getDefault(), getBundleLoader());
    }

    @NotNull
    public static ResourceBundle getProgressMessages() throws MissingResourceException {
        return ResourceBundle.getBundle(PROGRESS_MESSAGES, Locale.getDefault(), getBundleLoader());
    }

    @NotNull
    public static ResourceBundle getFXMLValues() throws MissingResourceException {
        return ResourceBundle.getBundle(FXML_VALUES, Locale.getDefault(), getBundleLoader());
    }

    @NotNull
    public static ResourceBundle getGeneralWords() throws MissingResourceException {
        return ResourceBundle.getBundle(GENERAL_WORDS, Locale.getDefault(), getBundleLoader());
    }

    @NotNull
    public static ResourceBundle getAlertMessages() throws MissingResourceException {
        return ResourceBundle.getBundle(ALERT_MESSAGES, Locale.getDefault(), getBundleLoader());
    }

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

}
