/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.i18n;

import com.dansoftware.boomega.plugin.Plugins;
import com.dansoftware.boomega.plugin.api.LanguagePlugin;
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

    private static final Map<Locale, List<LanguagePack>> loadedLanguagePacks = new LinkedHashMap<>();

    /**
     * The backing language-pack
     */
    private static volatile LanguagePack languagePack;

    static {
        loadPacks();
    }

    private I18N() {
    }

    public static LanguagePack getLanguagePack() {
        return languagePack;
    }

    public static Set<Locale> getAvailableLocales() {
        return loadedLanguagePacks.keySet();
    }

    public static Locale defaultLocale() {
        Set<Locale> available = getAvailableLocales();
        Locale systemDefault = Locale.getDefault();
        return available.contains(systemDefault) ? systemDefault : Locale.ENGLISH;
    }

    public static Map<Locale, Supplier<Collator>> getAvailableCollators() {
        var map = new HashMap<Locale, Supplier<Collator>>();
        loadedLanguagePacks.forEach((locale, languagePacks) ->
                languagePacks.stream()
                        .map(languagePack -> (Supplier<Collator>) languagePack::getABCCollator)
                        .findFirst()
                        .ifPresent(collatorSupplier -> map.put(locale, collatorSupplier))
        );
        return map;
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
            languagePack = getLanguagePackForLocale(Locale.getDefault()).orElseGet(EnglishLanguagePack::new);
    }

    private static void loadPacks() {
        registerBasePacks();
        registerPluginPacks();
    }

    private static void registerBasePacks() {
        putPack(Locale.ENGLISH, new EnglishLanguagePack());
        putPack(new Locale("hu"), new HungarianLanguagePack());
    }

    private static void registerPluginPacks() {
        logger.debug("Checking plugins for language-packs...");
        Plugins.getInstance().of(LanguagePlugin.class).stream()
                .map(LanguagePlugin::getLanguagePack)
                .peek(pack -> logger.debug("Found LanguagePack for locale '{}'", pack.getLocale()))
                .forEach(pack -> putPack(pack.getLocale(), pack));
    }

    private static void putPack(Locale locale, LanguagePack pack) {
        List<LanguagePack> list = loadedLanguagePacks.getOrDefault(locale, new ArrayList<>());
        list.add(pack);
        loadedLanguagePacks.put(locale, list);
    }

    private static Optional<LanguagePack> getLanguagePackForLocale(Locale locale) {
        return Optional.ofNullable(loadedLanguagePacks.getOrDefault(locale, new ArrayList<>() {{
            add(null);
        }}).get(0));
    }
}
