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

import com.dansoftware.boomega.util.Person;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Provides the {@link ResourceBundle} and other components required
 * for defining a language.
 */
public abstract class LanguagePack {

    private final Locale locale;
    private Person translator;

    protected LanguagePack(@NotNull Locale locale) {
        this.locale = Objects.requireNonNull(locale);
    }

    @SuppressWarnings("unused")
    protected LanguagePack(@NotNull Locale locale, @NotNull Person translator) {
        this(locale);
        this.translator = Objects.requireNonNull(translator);
    }

    public final @NotNull Locale getLocale() {
        return locale;
    }

    @Nullable
    public Person getTranslator() {
        return translator;
    }

    /**
     * Gives the bundle of internationalized values.
     */
    @NotNull
    public abstract ResourceBundle getValues();

    /**
     * Gives a {@link Collator} that can be used to compare strings based on
     * the rules of the language's alphabetic order
     */
    @NotNull
    public Collator getABCCollator() {
        return new NullHandlingCollator(Collator.getInstance(locale));
    }

    /**
     * @return {@code true} if the language represented by this pack is a
     *          <i>RIGHT TO LEFT</i> language (like arabic or hebrew);
     *          or {@code false} otherwise.
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
     * @return the full name
     */
    @NotNull
    public String displayPersonName(@Nullable String firstName, @Nullable String lastName) {
        return "%s %s".formatted(firstName, lastName);
    }

    /**
     * Concatenates the given person's first & lastnames according to the conventions of the language.
     *
     * @param person the person object
     * @return the full name
     */
    public String displayPersonName(@NotNull Person person) {
        final var firstNames = String.join(" ", person.getFirstNames());
        final var lastNames = String.join(" ", person.getLastNames());
        return displayPersonName(firstNames, lastNames);
    }
}
