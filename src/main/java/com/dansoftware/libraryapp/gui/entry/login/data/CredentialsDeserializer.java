package com.dansoftware.libraryapp.gui.entry.login.data;

import com.dansoftware.libraryapp.db.Credentials;
import com.google.gson.*;
import org.jasypt.util.text.StrongTextEncryptor;

import java.lang.reflect.Type;

public class CredentialsDeserializer implements JsonDeserializer<Credentials> {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENCRYPTION_PASSWORD = "encp";

    @Override
    public Credentials deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String username = jsonObject.get(USERNAME).getAsString();
        String password = jsonObject.get(PASSWORD).getAsString();
        String encryptionPassword = jsonObject.get(ENCRYPTION_PASSWORD).getAsString();

        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encryptionPassword);
        password = textEncryptor.decrypt(password);

        return new Credentials(username, password);
    }
}
