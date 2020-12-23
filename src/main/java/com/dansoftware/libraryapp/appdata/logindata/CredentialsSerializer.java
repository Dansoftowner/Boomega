package com.dansoftware.libraryapp.appdata.logindata;

import com.dansoftware.libraryapp.db.Credentials;
import com.google.gson.*;
import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import java.lang.reflect.Type;

public class CredentialsSerializer implements JsonSerializer<Credentials> {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
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
