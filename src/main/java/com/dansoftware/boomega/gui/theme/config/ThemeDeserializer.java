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

package com.dansoftware.boomega.gui.theme.config;

import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.plugin.PluginClassLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

public class ThemeDeserializer implements JsonDeserializer<Theme> {

    private static final Logger logger = LoggerFactory.getLogger(ThemeDeserializer.class);

    private ClassLoader getClassLoader() {
        return PluginClassLoader.getInstance();
    }

    private String getClassName(JsonElement jsonElement) {
        return jsonElement.getAsString();
    }

    private Class<?> getClassRef(String className) throws ClassNotFoundException {
        return getClassLoader().loadClass(className);
    }

    private Constructor<?> getThemeConstructor(Class<?> classRef) throws NoSuchMethodException {
        return classRef.getConstructor();
    }

    private Object getInstanceOf(Class<?> classRef) {
        try {
            Constructor<?> defaultConstructor = getThemeConstructor(classRef);
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkClassValid(Class<?> classRef) throws ThemeClassNotValidException {
        if (classRef == Theme.class) {
            throw new ThemeClassNotValidException(String.format("The class shouldn't be the abstract '%s' class", Theme.class));
        } else if (!Theme.class.isAssignableFrom(classRef)) {
            throw new ThemeClassNotValidException(String.format("The class '%s' is not a subtype of '%s'", classRef, Theme.class));
        }
    }

    private Theme getThemeInstance(JsonElement json) {
        try {
            Class<?> classRef = getClassRef(getClassName(json));
            checkClassValid(classRef);
            return (Theme) getInstanceOf(classRef);
        } catch (ClassNotFoundException e) {
            logger.error("The configured theme not found", e);
        } catch (ThemeClassNotValidException e) {
            logger.error("The configured theme is not valid", e);
        }

        return null;
    }

    @Override
    public Theme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return getThemeInstance(json);
    }

    private static final class ThemeClassNotValidException extends Exception {
        ThemeClassNotValidException(String msg) {
            super(msg);
        }
    }
}
