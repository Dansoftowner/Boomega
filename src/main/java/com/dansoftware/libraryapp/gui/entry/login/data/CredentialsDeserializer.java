package com.dansoftware.libraryapp.gui.entry.login.data;

import com.dansoftware.libraryapp.db.Credentials;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import java.lang.reflect.Type;

public class CredentialsDeserializer implements JsonDeserializer<Credentials> {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENCRYPTION_PASSWORD = "encp";

    @Override
    public Credentials deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement usernameJson = jsonObject.get(USERNAME);
        JsonElement passwordJson = jsonObject.get(PASSWORD);
        JsonElement encJson = jsonObject.get(ENCRYPTION_PASSWORD);

        String username = usernameJson != null ? usernameJson.getAsString() : null;
        String password = passwordJson != null ? passwordJson.getAsString() : null;
        String encryptionPassword = encJson != null ? encJson.getAsString() : null;

        if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(encryptionPassword)) {
            StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
            textEncryptor.setPassword(encryptionPassword);
            password = textEncryptor.decrypt(password);
        }

        return new Credentials(username, password);
    }
}
