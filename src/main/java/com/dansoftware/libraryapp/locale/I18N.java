package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.text.MessageFormat;
import java.util.*;

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

    private static LanguagePack languagePack;

    static {
        loadPacks();
    }

    private static void loadPacks() {
        //Collecting LanguagePacks from plugins
        if (!PluginClassLoader.getInstance().isEmpty()) {
            Reflections pluginReflections = new Reflections(new ConfigurationBuilder()
                    .addClassLoader(PluginClassLoader.getInstance())
                    .addUrls(ClasspathHelper.forClassLoader(PluginClassLoader.getInstance()))
                    .setScanners(new SubTypesScanner()));
            pluginReflections.getSubTypesOf(LanguagePack.class)
                    .forEach(classRef -> ReflectionUtils.initializeClass(classRef, PluginClassLoader.getInstance()));
        }

        //collecting LanguagePacks from the core project
        Reflections reflections = new Reflections(LanguagePack.class);
        reflections.getSubTypesOf(LanguagePack.class).forEach(ReflectionUtils::initializeClass);
    }

    public static LanguagePack getLanguagePack() {
        return languagePack;
    }

    public static Set<Locale> getAvailableLocales() {
        return LanguagePack.getSupportedLocales();
    }

    public static Optional<LanguagePack> getLanguagePackOf(@NotNull Locale locale) {
        return LanguagePack.getLanguagePacksForLocale(locale).stream()
                .map(ReflectionUtils::tryConstructObject)
                .filter(Objects::nonNull)
                .map(languagePack -> (LanguagePack) languagePack)
                .findFirst();
    }

    private static void recognizeLanguagePack() {
        if (languagePack == null || !languagePack.getLocale().equals(Locale.getDefault()))
            languagePack = getLanguagePackOf(Locale.getDefault()).orElseGet(EnglishLanguagePack::new);
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
    public static ResourceBundle getFXMLValues() {
        recognizeLanguagePack();
        return languagePack.getFXMLValues();
    }

    @NotNull
    public static ResourceBundle getGeneralWords() {
        recognizeLanguagePack();
        return languagePack.getGeneralWords();
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

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }
}
