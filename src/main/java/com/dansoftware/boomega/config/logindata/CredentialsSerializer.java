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

import com.dansoftware.boomega.db.Credentials;
import com.google.gson.*;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import java.lang.reflect.Type;

public class CredentialsSerializer implements JsonSerializer<Credentials> {

    private static final String USERNAME = "credentials.username";
    private static final String PASSWORD = "credentials.password";
    private static final String ENCRYPTION_PASSWORD = "encp";

    @Override
    public JsonElement serialize(Credentials src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isAnonymous()) {
            return JsonNull.INSTANCE;
        }
        return buildJsonObject(src);
    }

    private JsonObject buildJsonObject(Credentials src) {
        final String encp = Double.toString(Math.random());
        final TextEncryptor textEncryptor = buildEncryptor(encp);

        String username = src.getUsername();
        String password = src.getPassword();
        password = textEncryptor.encrypt(password);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(USERNAME, username);
        jsonObject.addProperty(PASSWORD, password);
        jsonObject.addProperty(ENCRYPTION_PASSWORD, encp);
        return jsonObject;
    }

    private TextEncryptor buildEncryptor(String encPass) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encPass);
        return textEncryptor;
    }
}
