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

import com.dansoftware.boomega.i18n.EnglishLanguagePack;
import com.dansoftware.boomega.plugin.LanguagePlugin;
import com.dansoftware.boomega.plugin.api.PluginService;
import com.dansoftware.boomega.util.ReflectionUtils;
import com.google.gson.JsonElement;
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
import static com.dansoftware.boomega.util.Collections.toImmutableList;
import static com.dansoftware.boomega.util.Resources.resJson;

/**
 * Used as a central gateway for accessing localized messages/values.
 * Also, it provides other utilities regarding localization/internationalization.
 */
public class I18N {

    private static final Logger logger = LoggerFactory.getLogger(I18N.class);

    /**
     * The path of the resource containing the internal language pack names
     */
    private static final String INTERNAL_LANGUAGE_PACKS = "internal_lang_packs.json";

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
        return available.contains(systemDefault) ? systemDefault : Locale.ENGLISH;
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
        recognizeLanguagePack();
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
     *         {@link false} otherwise.
     * @see LanguagePack#isRTL()
     */
    public static boolean isRTL() {
        return getLanguagePack().isRTL();
    }

    /**
     * Recognizes the required {@link LanguagePack} for the default {@link Locale}.
     */
    private static void recognizeLanguagePack() {
        if (languagePack == null || !languagePack.getLocale().equals(Locale.getDefault()))
            languagePack = getLanguagePackForLocale(Locale.getDefault()).orElseGet(EnglishLanguagePack::new);
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
        for (var pack : listInternalLanguagePacks()) {
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
     * Maps the given locale to the language-pack
     */
    private static Optional<LanguagePack> getLanguagePackForLocale(Locale locale) {
        return loadedLanguagePacks.getOrDefault(locale, Collections.emptyList()).stream().findFirst();
    }

    /**
     * @return the list of internal/base language-packs
     */
    private static List<LanguagePack> listInternalLanguagePacks() {
        return toImmutableList(resJson(INTERNAL_LANGUAGE_PACKS, I18N.class).getAsJsonArray())
                .stream()
                .map(JsonElement::getAsString)
                .map(ReflectionUtils::forName)
                .map(ReflectionUtils::tryConstructObject)
                .map(LanguagePack.class::cast)
                .toList();
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
