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
 * It allows to write data to a particular source.
 */
public interface ConfigSource {

    double getDouble(@NotNull String key, double defValue);

    int getInteger(@NotNull String key, int defValue);

    boolean getBoolean(@NotNull String key, boolean defValue);

    String getString(@NotNull String key, String defValue);

    <T> T get(@NotNull PreferenceKey<T> key);

    void remove(@NotNull String key);

    void remove(@NotNull PreferenceKey<?> key);

    void putBoolean(@NotNull String key, boolean value);

    void putString(@NotNull String key, String value);

    void putInteger(@NotNull String key, int value);

    void putDouble(@NotNull String key, double value);

    <T> void put(@NotNull PreferenceKey<T> key, T value);

    boolean isCreated();

    boolean isOpened();

    void reset() throws IOException;

    void commit() throws IOException;
}
