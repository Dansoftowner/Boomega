/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.i18n.api;

import com.dansoftware.boomega.plugin.LanguagePlugin;
import com.dansoftware.boomega.plugin.api.PluginService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.dansoftware.boomega.di.DIService.get;

/**
 * Used as a central gateway for accessing localized messages/values.
 * Also, it provides other utilities regarding localization/internationalization.
 */
public class I18N {

    private static final Logger logger = LoggerFactory.getLogger(I18N.class);

    /**
     * @see InternalLanguagePacksConfig
     */
    private static final InternalLanguagePacksConfig internalConfig = get(InternalLanguagePacksConfig.class);

    /**
     * Stores all the loaded language packs
     */
    private static final Map<Locale, List<LanguagePack>> loadedLanguagePacks = new LinkedHashMap<>();

    /**
     * The backing language-pack
     */
    private static volatile LanguagePack languagePack;

    static {
        loadPacks();

        // initializing the languagePack (for preventing null pointer exceptions)
        setLocale(Locale.getDefault());
    }

    private I18N() {
        // Not instantiable
    }

    /**
     * Gives the default locale used by the system, just like the {@link Locale#getDefault()}
     * method, except that it returns the default {@link Locale#ENGLISH} locale, if the system default
     * is not supported by any language-packs.
     *
     * @return the preferred locale used by the environment
     */
    public static Locale defaultLocale() {
        Set<Locale> available = getAvailableLocales();
        Locale systemDefault = Locale.getDefault();
        return available.contains(systemDefault) ? systemDefault : internalConfig.getFallbackLocale();
    }

    /**
     * Sets the default locale for this instance of the Java Virtual Machine (see {@link Locale#setDefault(Locale)}),
     * and searches for the right {@link LanguagePack} mapped to it. If no language-pack found for the given locale,
     * a fallback language-pack will be configured.
     *
     * @param locale the locale representing the language/area
     */
    public static synchronized void setLocale(@NotNull Locale locale) {
        logger.debug("Setting locale '{}' as default", locale);
        Locale.setDefault(locale);
        languagePack = recognizeLanguagePack();
    }

    /**
     * Recognizes the required {@link LanguagePack} for the default {@link Locale}.
     */
    private static LanguagePack recognizeLanguagePack() {
        Locale currentLocale = Locale.getDefault();
        if (languagePack == null || !languagePack.getLocale().equals(currentLocale)) {
            Optional<LanguagePack> foundLanguagePack = getLanguagePackForLocale(currentLocale);
            if (foundLanguagePack.isPresent()) {
                logger.debug("Found language-pack for locale '{}': '{}'", currentLocale, foundLanguagePack.get());
                return foundLanguagePack.get();
            } else {
                LanguagePack fallback = internalConfig.getFallbackLanguagePack();
                logger.debug(
                        "Couldn't find language-pack for locale '{}'; setting '{}' as default.",
                        currentLocale,
                        fallback.getClass().getName()
                );
                return fallback;
            }
        }
        return languagePack;
    }

    /**
     * Maps the given locale to the language-pack
     */
    private static Optional<LanguagePack> getLanguagePackForLocale(Locale locale) {
        return loadedLanguagePacks.getOrDefault(locale, Collections.emptyList()).stream().findFirst();
    }

    /**
     * Gives all the available {@link Collator}s for all the {@link Locale}s
     * supported by the app.
     * <p>
     * <p>
     * These collators can be used for ordering strings according to the language's
     * alphabetic order (ABC).
     * <p>
     * <p>
     * Each key in the resulting {@link Map} is a {@link Locale} representing the language;
     * and each value is a {@link Supplier} that can return the actual {@link Collator}.
     *
     * @return the map of locale and collator (wrapped in a {@link Supplier}) pairs
     */
    public static Map<Locale, Supplier<Collator>> getAvailableCollators() {
        return loadedLanguagePacks.values()
                .stream()
                .map(list -> list.isEmpty() ? null : list.get(0))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(LanguagePack::getLocale, it -> it::getABCCollator));
    }

    /**
     * Gets the resource-bundle currently used for localization.
     */
    @NotNull
    public static ResourceBundle getValues() {
        return languagePack.getValues();
    }

    /**
     * Gets the internationalized value from the default resource bundle.
     *
     * @param key  the property-key
     * @param args only has a role if the property is a pattern-string (see {@link MessageFormat}).
     * @return the property-value
     */
    @NotNull
    public static String getValue(
            @PropertyKey(resourceBundle = "com.dansoftware.boomega.i18n.Values") String key,
            @Nullable Object... args
    ) {
        try {
            return getValue(getValues(), key, args);
        } catch (MissingResourceException e) {
            logger.error("Couldn't find i18n value", e);
            return key;
        }
    }

    private static String getValue(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object[] args) {
        if (args == null || args.length == 0) return resourceBundle.getString(key);
        return getFormat(resourceBundle, key, args);
    }

    private static String getFormat(@NotNull ResourceBundle resourceBundle, @NotNull String key, Object... args) {
        return MessageFormat.format(resourceBundle.getString(key), args);
    }

    /**
     * @return {@code true} if the configured language is an RTL (right-to-left) language (e.g. Hebrew);
     * {@code false} otherwise.
     * @see LanguagePack#isRTL()
     */
    public static boolean isRTL() {
        return getLanguagePack().isRTL();
    }

    /**
     * Loads the internal & plugin language packs.
     */
    private static void loadPacks() {
        registerBasePacks();
        registerPluginPacks();
    }

    /**
     * Registers the internal language-packs.
     */
    private static void registerBasePacks() {
        for (var pack : internalConfig.getLanguagePacks()) {
            logger.debug("Registering internal language-pack '{}'", pack.getClass().getName());
            putPack(pack.getLocale(), pack);
        }
    }

    /**
     * Registers the language-packs loaded from plugins.
     */
    private static void registerPluginPacks() {
        logger.debug("Checking plugins for language-packs...");
        get(PluginService.class).of(LanguagePlugin.class).stream()
                .map(LanguagePlugin::getLanguagePack)
                .peek(pack -> logger.debug("Found LanguagePack for locale '{}'", pack.getLocale()))
                .forEach(pack -> putPack(pack.getLocale(), pack));
    }

    /**
     * Registers a language-pack for a locale
     */
    private static void putPack(Locale locale, LanguagePack pack) {
        List<LanguagePack> list = loadedLanguagePacks.getOrDefault(locale, new ArrayList<>());
        list.add(pack);
        loadedLanguagePacks.put(locale, list);
    }

    /**
     * Gets the default ABC collator for the given locale.
     */
    public static Optional<Collator> getABCCollator(@Nullable Locale locale) {
        return getLanguagePackForLocale(locale).map(LanguagePack::getABCCollator);
    }

    /**
     * @return the configured language-pack
     */
    public static LanguagePack getLanguagePack() {
        return languagePack;
    }

    /**
     * @return the set of supported locales (locales that have language-pack pack(s))
     */
    public static Set<Locale> getAvailableLocales() {
        return loadedLanguagePacks.keySet();
    }

}
