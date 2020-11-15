package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
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

    private static final Map<Locale, List<Class<? extends LanguagePack>>> languagePacksForLocales;

    static {
        languagePacksForLocales = Collections.synchronizedMap(new HashMap<>());
    }

    protected static void registerLanguagePack(@NotNull Locale locale, @NotNull Class<? extends LanguagePack> languagePackClass) {
        List<Class<? extends LanguagePack>> classes = languagePacksForLocales.get(locale);
        if (classes != null) {
            classes.add(languagePackClass);
        } else {
            languagePacksForLocales.put(locale, new LinkedList<>(List.of(languagePackClass)));
        }
    }

    @NotNull
    protected static List<Class<? extends LanguagePack>> getLanguagePacksForLocale(@NotNull Locale locale) {
        return Optional.ofNullable(languagePacksForLocales.get(locale)).orElseGet(Collections::emptyList);
    }

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
