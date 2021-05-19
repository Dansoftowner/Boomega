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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

/**
 * A {@link JsonSource} is a {@link ConfigSource} that can store the
 * configurations in JSON format.
 */
public abstract class JsonSource implements ConfigSource {

    private static final Logger logger = LoggerFactory.getLogger(JsonSource.class);

    private final Gson gson;

    public JsonSource() {
        this.gson = new Gson();
    }

    @Override
    public double getDouble(String key, double defValue) {
        JsonElement jsonElement = this.getJsonBase().get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsDouble();
    }

    @Override
    public int getInteger(String key, int defValue) {
        JsonElement jsonElement = this.getJsonBase().get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsInt();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        JsonElement jsonElement = this.getJsonBase().get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsBoolean();
    }

    @Override
    public String getString(String key, String defValue) {
        JsonElement jsonElement = this.getJsonBase().get(key);
        if (Objects.isNull(jsonElement)) {
            return defValue;
        }

        return jsonElement.getAsString();
    }

    @Override
    public <T> T get(PreferenceKey<T> key) {
        try {
            JsonElement jsonElement = this.getJsonBase().get(key.getJsonKey());
            if (jsonElement == null) {
                return key.getDefaultValue().get();
            }

            T value = key.getAdapter() == null ? gson.fromJson(jsonElement, key.getType()) :
                    key.getAdapter().deserialize(jsonElement, key.getType(), null);
            return Objects.isNull(value) ? key.getDefaultValue().get() : value;
        } catch (RuntimeException e) {
            logger.error("Couldn't parse value for '{}'", key.getJsonKey(), e);
            return key.getDefaultValue().get();
        }
    }

    @Override
    public void remove(String key) {
        getJsonBase().remove(key);
    }

    @Override
    public void remove(PreferenceKey<?> key) {
        remove(key.getJsonKey());
    }

    @Override
    public void putBoolean(String key, boolean value) {
        getJsonBase().addProperty(key, value);
    }

    @Override
    public void putString(String key, String value) {
        getJsonBase().addProperty(key, value);
    }

    @Override
    public void putInteger(String key, int value) {
        getJsonBase().addProperty(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        getJsonBase().addProperty(key, value);
    }

    @Override
    public <T> void put(PreferenceKey<T> key, T value) {
        JsonElement element = null;
        if (value != null)
            element = key.getAdapter() == null ? gson.toJsonTree(value, key.getType()) :
                    key.getAdapter().serialize(value, value.getClass(), null);
        getJsonBase().add(key.getJsonKey(), element);
    }

    /**
     * Should return the {@link JsonObject} that stores the configurations.
     * This method will be called everytime when the {@link JsonSource}
     * wants to access this object.
     *
     * @return the object
     */
    protected abstract JsonObject getJsonBase();

    @Override
    public void reset() throws IOException {
        JsonObject json = getJsonBase();
        var keys = new HashSet<>(json.keySet());
        keys.forEach(json::remove);
        commit();
    }
}
