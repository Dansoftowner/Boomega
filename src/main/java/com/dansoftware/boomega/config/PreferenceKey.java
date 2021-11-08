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

package com.dansoftware.boomega.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A {@link PreferenceKey} is an accessor to a particular configuration.
 *
 * @param <T> the type of the object that can be accessed by the key
 */
public class PreferenceKey<T> {


    private final String jsonKey;
    private final Class<T> type;
    private final Supplier<T> defaultValue;

    private ConfigAdapter<T> adapter;

    public PreferenceKey(@NotNull String jsonKey,
                         @NotNull Class<T> type,
                         @NotNull Supplier<T> defaultValue) {
        this.jsonKey = jsonKey;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public PreferenceKey(@NotNull String jsonKey,
                         @NotNull Class<T> type,
                         @Nullable ConfigAdapter<T> adapter,
                         @NotNull Supplier<T> defaultValue) {
        this.jsonKey = jsonKey;
        this.type = type;
        this.adapter = adapter;
        this.defaultValue = defaultValue;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public Class<T> getType() {
        return type;
    }

    public Supplier<T> getDefaultValue() {
        return defaultValue;
    }

    public ConfigAdapter<T> getAdapter() {
        return adapter;
    }

    public static <T> PreferenceKey<T> createKey(@NotNull Class<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return new PreferenceKey<>(type.getName(), type, defaultValue);
    }
}
