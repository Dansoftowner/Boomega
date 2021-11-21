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
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import java.lang.reflect.Type;

public class CredentialsDeserializer implements JsonDeserializer<Credentials> {

    private static final String USERNAME = "credentials.username";
    private static final String PASSWORD = "credentials.password";
    private static final String ENCRYPTION_PASSWORD = "encp";

    @Override
    public Credentials deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return buildCredentials(json.getAsJsonObject());
    }

    private Credentials buildCredentials(JsonObject jsonObject) {
        JsonElement usernameJson = jsonObject.get(USERNAME);
        JsonElement passwordJson = jsonObject.get(PASSWORD);
        JsonElement encJson = jsonObject.get(ENCRYPTION_PASSWORD);

        String username = usernameJson != null ? usernameJson.getAsString() : null;
        String password = passwordJson != null ? passwordJson.getAsString() : null;
        String encryptionPassword = encJson != null ? encJson.getAsString() : null;

        if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(encryptionPassword)) {
            final TextEncryptor textEncryptor = buildEncryptor(encryptionPassword);
            password = textEncryptor.decrypt(password);
        }

        return new Credentials(username, password);
    }

    private TextEncryptor buildEncryptor(String encp) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encp);
        return textEncryptor;
    }
}