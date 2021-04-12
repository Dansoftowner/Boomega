package com.dansoftware.boomega.config.theme;

import com.dansoftware.boomega.gui.theme.Theme;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return themeSerializer.serialize(src, typeOfSrc, context);
    }

    @Override
    public Theme deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return themeDeserializer.deserialize(json, typeOfT, context);
    }
}
