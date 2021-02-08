package com.dansoftware.libraryapp.i18n;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

/**
 * A {@link LanguagePack} provides {@link ResourceBundle}s for a particular {@link Locale}.
 *
 * @author Daniel Gyorffy
 */
public abstract class LanguagePack {

    private static final Logger logger = LoggerFactory.getLogger(LanguagePack.class);

    private static final Map<Locale, List<LanguagePack>> languagePacksForLocales =
            Collections.synchronizedMap(new HashMap<>());

    /**
     * Registers a {@link LanguagePack} to {@link Locale}.
     *
     * @param locale       the locale; shouldn't be null
     * @param languagePack the language pack; shouldn't be null
     * @throws NullPointerException if one of the arguments is null
     */
    public static void registerLanguagePack(@NotNull Locale locale,
                                            @NotNull LanguagePack languagePack) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(languagePack);
        List<LanguagePack> packs = languagePacksForLocales.get(locale);
        if (packs != null)
            packs.add(languagePack);
        else
            languagePacksForLocales.put(locale, new LinkedList<>(Collections.singletonList(languagePack)));
        logger.debug("Registered language pack with locale: {}", locale);
    }

    /**
     * Lists all the registered {@link LanguagePack} types for the particular
     * {@link Locale}.
     *
     * @param locale the locale
     * @return the {@link List} of {@link Class} objects that are representing the {@link LanguagePack} types;
     * if the given locale isn't supported, then it will return an empty list
     */
    @NotNull
    public static List<LanguagePack> getLanguagePacksForLocale(@Nullable Locale locale) {
        return Optional.ofNullable(languagePacksForLocales.get(locale))
                .map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);
    }

    @NotNull
    protected static Optional<LanguagePack> getLanguagePackForLocale(@Nullable Locale locale) {
        return getLanguagePacksForLocale(locale).stream().findFirst();
    }

    /**
     * Finds a {@link Collator} for the given locale
     *
     * @param locale the locale
     * @return the Collator object
     */
    protected static Collator getCollator(@NotNull Locale locale) {
        return Optional.ofNullable(languagePacksForLocales.get(locale))
                .flatMap(it -> it.stream().findFirst())
                .map(LanguagePack::getABCCollator)
                .orElseGet(() -> Collator.getInstance(locale));
    }

    protected static Map<Locale, Supplier<Collator>> getAvailableCollators() {
        var map = new HashMap<Locale, Supplier<Collator>>();
        languagePacksForLocales.forEach((locale, languagePacks) ->
                languagePacks.stream()
                        .map(languagePack -> (Supplier<Collator>) languagePack::getABCCollator)
                        .findFirst()
                        .ifPresent(collatorSupplier -> map.put(locale, collatorSupplier))
        );
        return map;
    }

    /**
     * Lists the registered {@link Locale}s that are supported.
     *
     * @return the {@link Set} of locales
     */
    protected static Set<Locale> getSupportedLocales() {
        return languagePacksForLocales.keySet();
    }

    private final Locale locale;
    private LanguageTranslator languageTranslator;

    protected LanguagePack(@NotNull Locale locale) {
        this.locale = Objects.requireNonNull(locale);
    }

    protected LanguagePack(@NotNull Locale locale, @NotNull LanguageTranslator languageTranslator) {
        this(locale);
        this.languageTranslator = Objects.requireNonNull(languageTranslator);
    }

    public final @NotNull Locale getLocale() {
        return locale;
    }

    @Nullable
    public LanguageTranslator getTranslator() {
        return languageTranslator;
    }

    protected abstract Collator getABCCollator();

    /**
     * Returns {@code true} if the language represented by this pack is a <i>RIGHT TO LEFT</i>
     * language like arabic or hebrew or not.
     *
     * @return {@code true} if the language is RTL; {@code false} otherwise
     */
    protected abstract boolean isRTL();

    @NotNull
    public abstract ResourceBundle getValues();

    protected ResourceBundle getBundle(String path) {
        return ResourceBundle.getBundle(path, locale, PluginClassLoader.getInstance());
    }
}
