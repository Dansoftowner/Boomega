package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A {@link LanguagePack} provides {@link ResourceBundle}s for a particular {@link Locale}.
 *
 * @author Daniel Gyorffy
 */
public abstract class LanguagePack {

    private static final Logger logger = LoggerFactory.getLogger(LanguagePack.class);

    private static final Map<Locale, List<Class<? extends LanguagePack>>> languagePacksForLocales =
            Collections.synchronizedMap(new HashMap<>());

    /**
     * Registers a {@link LanguagePack} to {@link Locale}.
     *
     * @param locale            the locale; shouldn't be null
     * @param languagePackClass the language pack; shouldn't be null
     * @throws NullPointerException if one of the arguments is null
     */
    protected static void registerLanguagePack(@NotNull Locale locale,
                                               @NotNull Class<? extends LanguagePack> languagePackClass) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(languagePackClass);
        List<Class<? extends LanguagePack>> classes = languagePacksForLocales.get(locale);
        if (classes != null)
            classes.add(languagePackClass);
        else
            languagePacksForLocales.put(locale, new LinkedList<>(Collections.singletonList(languagePackClass)));
        logger.debug("Registered locale: {} with languagePack: {}", locale, languagePackClass);
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
    protected static List<Class<? extends LanguagePack>> getLanguagePacksForLocale(@Nullable Locale locale) {
        List<Class<? extends LanguagePack>> packs = languagePacksForLocales.get(locale);
        return packs == null ? Collections.emptyList() : Collections.unmodifiableList(packs);
    }

    /**
     * Searches for a {@link LanguagePack} implementation for the given {@link Locale}
     * and instantiates it if possible.
     *
     * @param locale the locale
     * @return the result wrapped in an {@link Optional}
     */
    public static Optional<LanguagePack> instantiateLanguagePack(@Nullable Locale locale) {
        return getLanguagePacksForLocale(locale).stream()
                .map(ReflectionUtils::tryConstructObject)
                .filter(Objects::nonNull)
                .map(languagePack -> (LanguagePack) languagePack)
                .findFirst();
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

    protected ResourceBundle getBundle(String path) {
        return ResourceBundle.getBundle(path, locale, PluginClassLoader.getInstance());
    }

    /**
     * Returns {@code true} if the language represented by this pack is a <i>RIGHT TO LEFT</i>
     * language like arabic or hebrew or not.
     *
     * @return {@code true} if the language is RTL; {@code false} otherwise
     */
    protected abstract boolean isRTL();

    @NotNull
    public abstract ResourceBundle getButtonTypeValues();

    @NotNull
    protected abstract ResourceBundle getWindowTitles();

    @NotNull
    protected abstract ResourceBundle getFirstTimeDialogValues();

    @NotNull
    protected abstract ResourceBundle getProgressMessages();

    @NotNull
    protected abstract ResourceBundle getFXMLValues();

    @NotNull
    protected abstract ResourceBundle getGeneralWords();

    @NotNull
    protected abstract ResourceBundle getAlertMessages();

    @NotNull
    protected abstract ResourceBundle getUpdateDialogValues();
}
