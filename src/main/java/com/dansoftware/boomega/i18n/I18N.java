package com.dansoftware.boomega.i18n;

import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.dansoftware.boomega.util.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Supplier;

/**
 * Used for accessing localized messages/values.
 */
public class I18N {

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

    public static Map<Locale, Supplier<Collator>> getAvailableCollators() {
        return LanguagePack.getAvailableCollators();
    }

    @NotNull
    public static String getValue(@NotNull String key, @Nullable Object... args) throws MissingResourceException {
        return getValue(getValues(), key, args);
    }

    private static String getValue(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object[] args) {
        if (ArrayUtils.isEmpty(args)) return resourceBundle.getString(key);
        return getFormat(resourceBundle, key, args);
    }

    @NotNull
    public static ResourceBundle getValues() {
        recognizeLanguagePack();
        return languagePack.getValues();
    }

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

    /**
     * Recognizes the required {@link LanguagePack} for the default {@link LanguagePack}.
     */
    private static void recognizeLanguagePack() {
        if (languagePack == null || !languagePack.getLocale().equals(Locale.getDefault()))
            languagePack = LanguagePack.getLanguagePackForLocale(Locale.getDefault()).orElseGet(EnglishLanguagePack::new);
    }

    /**
     * Initializes the {@link LanguagePack} classes.
     */
    private static void loadPacks() {
        //Collecting LanguagePacks from the core project
        ReflectionUtils.getSubtypesOf(LanguagePack.class).forEach(ReflectionUtils::initializeClass);
        PluginClassLoader.getInstance().initializeSubtypeClasses(LanguagePack.class);
    }

    private I18N() {
    }
}
