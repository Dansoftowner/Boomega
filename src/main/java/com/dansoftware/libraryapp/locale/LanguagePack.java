package com.dansoftware.libraryapp.locale;

import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    protected ResourceBundle getBundle(String path) {
        return ResourceBundle.getBundle(path, locale, PluginClassLoader.getInstance());
    }

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
