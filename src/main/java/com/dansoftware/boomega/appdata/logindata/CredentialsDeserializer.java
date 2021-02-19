package com.dansoftware.boomega.appdata.logindata;

import com.dansoftware.boomega.db.Credentials;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import java.lang.reflect.Type;

public class CredentialsDeserializer implements JsonDeserializer<Credentials> {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
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
