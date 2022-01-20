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

package com.dansoftware.boomega.config.source;

import com.dansoftware.boomega.config.PreferenceKey;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * A {@link ConfigSource} represents a source for a {@link com.dansoftware.boomega.config.Preferences} object.
 */
public interface ConfigSource {

    /**
     * Gets a double value.
     *
     * @param key the key for the preference
     * @param defValue the default value to return if no value is mapped to the given key
     * @return the {@code double} value
     */
    double getDouble(@NotNull String key, double defValue);

    /**
     * Gets an integer value.
     *
     * @param key the key for the preference
     * @param defValue the default value to return if no value is mapped to the given key
     * @return the {@code int} value
     */
    int getInteger(@NotNull String key, int defValue);

    /**
     * Gets a boolean value.
     *
     * @param key the key for the preference
     * @param defValue the default value to return if no value is mapped to the key
     * @return the {@code boolean} value
     */
    boolean getBoolean(@NotNull String key, boolean defValue);

    /**
     * Gets a string value.
     *
     * @param key the key for the preference
     * @param defValue the default value to return if no value is mapped to the key
     * @return the {@link String} value
     */
    String getString(@NotNull String key, String defValue);

    /**
     * Gets a value by a {@link PreferenceKey} object.
     *
     * @param key the key for the preference
     * @param <T> the type of the return value
     * @return the value for the given key
     */
    <T> T get(@NotNull PreferenceKey<T> key);

    /**
     * Removes an entry by its key.
     *
     * @param key the key of the preference
     */
    void remove(@NotNull String key);

    /**
     * Removes an entry by its key.
     *
     * @param key the key of the preference
     */
    void remove(@NotNull PreferenceKey<?> key);

    /**
     * Maps the {@code boolean} value to the given key.
     * If a value already exists with that key,
     * it will simply overwrite it.
     *
     * @param key the key of the preference
     */
    void putBoolean(@NotNull String key, boolean value);

    /**
     * Maps the {@code String} value to the given key.
     * If a value already exists with that key,
     * it will simply overwrite it.
     *
     * @param key the key of the preference
     */
    void putString(@NotNull String key, String value);

    /**
     * Maps the {@code int} value to the given key.
     * If a value already exists with that key,
     * it will simply overwrite it.
     *
     * @param key the key of the preference
     */
    void putInteger(@NotNull String key, int value);

    /**
     * Maps the {@code double} value to the given key.
     * If a value already exists with that key,
     * it will simply overwrite it.
     *
     * @param key the key of the preference
     */
    void putDouble(@NotNull String key, double value);

    /**
     * Maps the given value to the given key.
     * If a value already exists with that key,
     * it will simply overwrite it.
     *
     * @param key the key of the preference
     * @param <T> the type of the value
     */
    <T> void put(@NotNull PreferenceKey<T> key, T value);

    /**
     * Determines whether the particular data-source (e.g. a file) was existent before
     * or it had to be created.
     *
     * Primarily it's useful for a {@link ConfigSource} that operates
     * with file data-sources.
     *
     * @return {@code true} if the data source didn't exist; {@code false} otherwise.
     */
    boolean isCreated();

    /**
     * Determines whether the particular data-source was existed before
     * this object was created or not.
     *
     * @return {@code true} if the data source did exist; {@code false} otherwise.
     */
    boolean isOpened();

    /**
     * Removes all the data. <b>Use it carefully!</b>
     *
     * @throws IOException if some I/O exception occurs
     */
    void reset() throws IOException;

    /**
     * Persists the mutations of the data performed in the memory
     * (e.g. writes the modifications to a file in case of a file-based data source).
     *
     * @throws IOException if some I/O exception occurs
     */
    void commit() throws IOException;
}
