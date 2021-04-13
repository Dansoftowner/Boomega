package com.dansoftware.boomega.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A {@link LanguagePack} provides {@link ResourceBundle}s for a particular {@link Locale}.
 *
 * @author Daniel Gyorffy
 */
public abstract class LanguagePack {

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
        return ResourceBundle.getBundle(path, locale);
    }
}
