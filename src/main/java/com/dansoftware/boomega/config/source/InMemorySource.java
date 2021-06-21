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
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * An {@link InMemorySource} is a {@link ConfigSource} that stores data
 * only in the memory.
 */
@TestOnly
public class InMemorySource implements ConfigSource {

    private final Map<String, Object> map;

    public InMemorySource() {
        this.map = new HashMap<>();
    }

    @Override
    public double getDouble(String key, double defValue) {
        Object o = this.map.get(key);
        return o == null ? defValue : (double) o;
    }

    @Override
    public int getInteger(String key, int defValue) {
        Object o = this.map.get(key);
        return o == null ? defValue : (int) o;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object o = this.map.get(key);
        return o == null ? defValue : (boolean) o;
    }

    @Override
    public String getString(String key, String defValue) {
        Object o = this.map.get(key);
        return o == null ? defValue : (String) o;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(PreferenceKey<T> key) {
        Object o = this.map.get(key.getJsonKey());
        return o == null ? key.getDefaultValue().get() : (T) o;
    }

    @Override
    public void remove(String key) {
        this.map.remove(key);
    }

    @Override
    public void remove(PreferenceKey<?> key) {
        this.map.remove(key.getJsonKey());
    }

    @Override
    public void putBoolean(String key, boolean value) {
        this.map.put(key, value);
    }

    @Override
    public void putString(String key, String value) {
        this.map.put(key, value);
    }

    @Override
    public void putInteger(String key, int value) {
        this.map.put(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        this.map.put(key, value);
    }

    @Override
    public <T> void put(PreferenceKey<T> key, T value) {
        this.map.put(key.getJsonKey(), value);
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void reset() throws IOException {
        map.clear();
    }

    @Override
    public void commit() throws IOException {
    }
}
