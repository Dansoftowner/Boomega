package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Used for accessing localized messages/values.
 */
public class I18N {

    private I18N() {
    }

    private static final Logger logger = LoggerFactory.getLogger(I18N.class);

    /**
     * The backing language-pack
     */
    private static volatile LanguagePack languagePack;

    static {
        loadPacks();
    }

    public static LanguagePack getLanguagePack() {
        return languagePack;
    }

    public static Set<Locale> getAvailableLocales() {
        return LanguagePack.getSupportedLocales();
    }

    @NotNull
    public static String getGeneralWord(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        return getValue(getGenerals(), key, args);
    }

    @NotNull
    public static String getAlertMsg(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        return getValue(getAlertMessages(), key, args);
    }

    @NotNull
    public static String getProgressMessage(@NotNull String key, @Nullable Object... args) {
        return getValue(getProgressMessages(), key, args);
    }

    @NotNull
    public static String getPluginManagerValue(@NotNull String key, @Nullable Object... args) {
        return getValue(getPluginManagerValues(), key, args);
    }

    @NotNull
    public static String getLoginViewValue(@NotNull String key, @Nullable Object... args) {
        return getValue(getLoginViewValues(), key, args);
    }

    @NotNull
    public static String getDatabaseCreatorValue(@NotNull String key, @Nullable Object... args) {
        return getValue(getDatabaseCreatorViewValues(), key, args);
    }

    @NotNull
    public static String getDatabaseManagerValue(@NotNull String key, @Nullable Object... args) {
        return getValue(getDatabaseManagerValues(), key, args);
    }

    @NotNull
    public static String getUpdateDialogValue(@NotNull String key, @Nullable Object... args) {
        return getValue(getUpdateDialogValues(), key, args);
    }

    private static String getValue(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object[] args) {
        if (ArrayUtils.isEmpty(args)) return resourceBundle.getString(key);
        return getFormat(resourceBundle, key, args);
    }

    @NotNull
    public static ResourceBundle getButtonTypeValues() {
        recognizeLanguagePack();
        return languagePack.getButtonTypeValues();
    }

    public static ResourceBundle getWindowTitles() {
        recognizeLanguagePack();
        return languagePack.getWindowTitles();
    }

    @NotNull
    public static ResourceBundle getFirstTimeDialogValues() {
        recognizeLanguagePack();
        return languagePack.getFirstTimeDialogValues();
    }

    @NotNull
    public static ResourceBundle getProgressMessages() {
        recognizeLanguagePack();
        return languagePack.getProgressMessages();
    }

    @NotNull
    public static ResourceBundle getGenerals() {
        recognizeLanguagePack();
        return languagePack.getGenerals();
    }

    @NotNull
    public static ResourceBundle getAlertMessages() {
        recognizeLanguagePack();
        return languagePack.getAlertMessages();
    }

    @NotNull
    public static ResourceBundle getUpdateDialogValues() {
        recognizeLanguagePack();
        return languagePack.getUpdateDialogValues();
    }

    @NotNull
    public static ResourceBundle getLoginViewValues() {
        recognizeLanguagePack();
        return languagePack.getLoginViewValues();
    }

    @NotNull
    public static ResourceBundle getInfoViewValues() {
        recognizeLanguagePack();
        return languagePack.getInfoViewValues();
    }

    @NotNull
    public static ResourceBundle getDatabaseCreatorViewValues() {
        recognizeLanguagePack();
        return languagePack.getDatabaseCreatorValues();
    }

    @NotNull
    public static ResourceBundle getPluginManagerValues() {
        recognizeLanguagePack();
        return languagePack.getPluginManagerValues();
    }

    @NotNull
    public static ResourceBundle getDatabaseManagerValues() {
        recognizeLanguagePack();
        return languagePack.getDatabaseManagerValues();
    }

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

    /**
     * Recognizes the required {@link LanguagePack} for the default {@link LanguagePack}.
     */
    private static void recognizeLanguagePack() {
        if (languagePack == null || !languagePack.getLocale().equals(Locale.getDefault()))
            languagePack = LanguagePack.instantiateLanguagePack(Locale.getDefault()).orElseGet(EnglishLanguagePack::new);
    }

    /**
     * Initializes the {@link LanguagePack} classes.
     */
    private static void loadPacks() {
        //Collecting LanguagePacks from the core project
        ReflectionUtils.getSubtypesOf(LanguagePack.class).forEach(ReflectionUtils::initializeClass);

        //Collecting LanguagePacks from plugins
        if (!PluginClassLoader.getInstance().isEmpty()) {
            ReflectionUtils.getSubtypesOf(LanguagePack.class, PluginClassLoader.getInstance()).forEach(classRef -> {
                try {
                    ReflectionUtils.initializeClass(classRef, PluginClassLoader.getInstance());
                } catch (ExceptionInInitializerError e) {
                    logger.error("Failed to initialize a language pack called: {}", classRef.getName(), e);
                }
            });
        }
    }
}
