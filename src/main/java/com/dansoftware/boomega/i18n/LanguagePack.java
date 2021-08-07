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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.CollationKey;
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

    /**
     * Gives the bundle for the language this {@link LanguagePack} represents.
     *
     * @return the {@link ResourceBundle} object.
     */
    @NotNull
    public abstract ResourceBundle getValues();

    /**
     * Gives a {@link Collator} that can be used to compare strings based on the
     * the rules of the language's alphabetic order
     *
     * @return the {@link Collator} object
     */
    @NotNull
    public Collator getABCCollator() {
        return new NullHandlingCollator(Collator.getInstance(locale));
    }

    /**
     * Returns {@code true} if the language represented by this pack is a <i>RIGHT TO LEFT</i>
     * language like arabic or hebrew or not.
     *
     * @return {@code true} if the language is RTL; {@code false} otherwise
     */
    protected abstract boolean isRTL();

    /**
     * Gets a resource bundle using the specified base name and the locale
     * of the language pack.
     *
     * @see ResourceBundle#getBundle(String, Locale)
     */
    @SuppressWarnings("SameParameterValue")
    protected ResourceBundle getBundle(String baseName) {
        return ResourceBundle.getBundle(baseName, locale);
    }

    /**
     * Concatenates the given first name and last name according to the conventions of the
     * language.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @return the concatenated name
     */
    @NotNull
    public String displayPersonName(@Nullable String firstName, @Nullable String lastName) {
        return String.format("%s %s", firstName, lastName);
    }
}
