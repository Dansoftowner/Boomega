package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections8.Reflections;

import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    public static LanguagePack getLanguagePack() {
        return languagePack;
    }

    public static Set<Class<? extends LanguagePack>> getAvailableLanguagePacks() {
        /*Reflections reflections = new Reflections(new ConfigurationBuilder().addScanners(new SubTypesScanner())
                .addClassLoaders(ClassLoader.getSystemClassLoader(), PluginClassLoader.getInstance()));*/
        Reflections reflections = new Reflections("", PluginClassLoader.getInstance());
        return reflections.getSubTypesOf(LanguagePack.class).stream()
                .filter(classRef -> !Modifier.isAbstract(classRef.getModifiers()))
                .collect(Collectors.toSet());
    }

    public static List<Locale> getAvailableLocales() {
        return getAvailableLanguagePacks().stream()
                .map(ReflectionUtils::tryConstructObject)
                .filter(Objects::nonNull)
                .map(LanguagePack::getLocale)
                .collect(Collectors.toList());
    }

    public static Optional<LanguagePack> getLanguagePackOf(@NotNull Locale locale) {
        return getAvailableLanguagePacks().stream()
                .map(ReflectionUtils::tryConstructObject)
                .filter(Objects::nonNull)
                .filter(languagePack -> languagePack.getLocale().equals(locale))
                .map(languagePack -> (LanguagePack) languagePack)
                .findFirst();
    }

    private static void recognizeLanguagePack() {
        if (languagePack == null)
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
