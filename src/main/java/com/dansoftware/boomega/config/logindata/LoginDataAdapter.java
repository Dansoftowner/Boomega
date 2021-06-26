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

package com.dansoftware.boomega.config.logindata;

import com.dansoftware.boomega.config.ConfigAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

public class LoginDataAdapter implements ConfigAdapter<LoginData> {

    private final LoginDataSerializer serializer;
    private final LoginDataDeserializer deserializer;

    public LoginDataAdapter() {
        this.serializer = new LoginDataSerializer();
        this.deserializer = new LoginDataDeserializer();
    }

    @Override
    public LoginData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return deserializer.deserialize(json, typeOfT, context);
    }

    @Override
    public JsonElement serialize(LoginData src, Type typeOfSrc, JsonSerializationContext context) {
        return serializer.serialize(src, typeOfSrc, context);
    }
}
