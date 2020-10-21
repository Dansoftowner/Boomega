package com.dansoftware.libraryapp.appdata.theme;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * A ThemeAdapter is used for serializing/deserializing the {@link Theme}.
 *
 * @author Daniel Gyorffy
 */
public class ThemeAdapter implements JsonSerializer<Theme>, JsonDeserializer<Theme> {

    private static final Logger logger = LoggerFactory.getLogger(ThemeAdapter.class);

    private final ThemeSerializer themeSerializer;
    private final ThemeDeserializer themeDeserializer;

    public ThemeAdapter() {
        this.themeSerializer = new ThemeSerializer();
        this.themeDeserializer = new ThemeDeserializer();
    }

    @Override
    public JsonElement serialize(Theme src, Type typeOfSrc, JsonSerializationContext context) {
        return themeSerializer.serialize(src);
    }

    @Override
    public Theme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return themeDeserializer.deserialize(json);
    }

    private static final class ThemeClassNotValidException extends Exception {
        ThemeClassNotValidException(String msg) {
            super(msg);
        }
    }

    private static final class ThemeSerializer {
        JsonElement serialize(Theme src) {
            return new JsonPrimitive(src.getClass().getName());
        }
    }

    private static final class ThemeDeserializer {

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

        Theme deserialize(JsonElement json) {
            return getThemeInstance(json);
        }
    }
}
