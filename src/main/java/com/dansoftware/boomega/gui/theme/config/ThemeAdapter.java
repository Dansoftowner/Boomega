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

import com.dansoftware.boomega.config.ConfigAdapter;
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
public class ThemeAdapter implements ConfigAdapter<Theme> {

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
