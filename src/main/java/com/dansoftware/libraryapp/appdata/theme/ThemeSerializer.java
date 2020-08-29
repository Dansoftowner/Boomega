package com.dansoftware.libraryapp.appdata.theme;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * A ThemeSerializer is used for converting the java object {@link Theme} into json-format to store it
 * as a configuration.
 *
 * @author Daniel Gyorffy
 */
public class ThemeSerializer implements JsonSerializer<Theme> {
    @Override
    public JsonElement serialize(Theme src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getClass().getName());
    }
}
