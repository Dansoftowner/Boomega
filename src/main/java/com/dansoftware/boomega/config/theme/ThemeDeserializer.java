package com.dansoftware.boomega.config.theme;

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
