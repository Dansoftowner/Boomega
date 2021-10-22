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

import com.dansoftware.boomega.config.logindata.LoginData;
import com.dansoftware.boomega.config.logindata.LoginDataAdapter;
import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.gui.theme.config.ThemeAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Locale;
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

    /**
     * Key for accessing the default locale.
     */
    @Deprecated
    public static final PreferenceKey<@NotNull Locale> LOCALE = new PreferenceKey<>("locale", Locale.class, Locale::getDefault);

    /**
     * Key for accessing the login data
     */
    @Deprecated
    public static final PreferenceKey<@NotNull LoginData> LOGIN_DATA = new PreferenceKey<>("loginData", LoginData.class, new LoginDataAdapter(), LoginData::new);

    /**
     * Key for accessing that the automatic update-searching is turned on or off
     */
    @Deprecated
    public static final PreferenceKey<@NotNull Boolean> SEARCH_UPDATES = new PreferenceKey<>("searchUpdates", Boolean.class, () -> Boolean.TRUE);

    /**
     * Key for accessing the configured theme
     */
    @Deprecated
    public static final PreferenceKey<@NotNull Theme> THEME = new PreferenceKey<>("theme", Theme.class, new ThemeAdapter(), Theme::getDefault);

    /**
     * Key for accessing the time of the last update-search
     */
    @Deprecated
    public static final PreferenceKey<@Nullable LocalDateTime> LAST_UPDATE_SEARCH = new PreferenceKey<>("searchUpdates.last", LocalDateTime.class, () -> null);
}
