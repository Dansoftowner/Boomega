package com.dansoftware.libraryapp.appdata.theme;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * A ThemeDeserializer is used for constructing the serialized {@link Theme} object from configuration json-file.
 *
 * @author Daniel Gyorffy
 */
public class ThemeDeserializer implements JsonDeserializer<Theme> {

    private static final Logger logger = LoggerFactory.getLogger(ThemeDeserializer.class);

    @Override
    public Theme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //the class-loader that can load classes from the class-path and from the plugin archives too
        ClassLoader classLoader = PluginClassLoader.getInstance();
        String className = json.getAsString(); //should be package.ClassName

        Class<?> themeClass;
        try {
            themeClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            logger.error("the Theme with the path '" + className + "' not found", e);
            return null;
        }

        if (themeClass == Theme.class) {
            logger.error("the abstract Theme shouldn't be specified as a used theme");
            return null;
        } else if (!Theme.class.isAssignableFrom(themeClass)) {
            logger.error("the specified theme '{}' is not an implementation of '{}'", themeClass, Theme.class);
            return null;
        }

        //at this point, we know that the loaded class is a subtype of Theme

        Constructor<?> defaultConstructor;
        try {
            defaultConstructor = themeClass.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.error("no default constructor found for '{}'", themeClass);
            return null;
        }

        //at this point, we found an empty constructor for the class, we can instantiate it
        Object instance;
        try {
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            return null;
        }

        return (Theme) instance;
    }
}
