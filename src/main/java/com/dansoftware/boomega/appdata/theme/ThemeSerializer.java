package com.dansoftware.boomega.appdata.theme;

import com.dansoftware.boomega.gui.theme.Theme;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ThemeSerializer implements JsonSerializer<Theme> {
    @Override
    public JsonElement serialize(Theme src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getClass().getName());
    }
}
