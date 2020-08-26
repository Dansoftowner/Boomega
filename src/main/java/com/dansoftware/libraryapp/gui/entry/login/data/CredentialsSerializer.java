package com.dansoftware.libraryapp.gui.entry.login.data;

import com.dansoftware.libraryapp.db.Credentials;
import com.google.gson.*;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class CredentialsSerializer implements JsonSerializer<Credentials> {

    private static final Logger logger = LoggerFactory.getLogger(CredentialsSerializer.class);

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENCRYPTION_PASSWORD = "encp";

    @Override
    public JsonElement serialize(Credentials src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isAnonymous()) {
            return JsonNull.INSTANCE;
        }

        String encryptionPassword = Double.toString(Math.random());
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encryptionPassword);

        String username = src.getUsername();
        String encryptedPassword = textEncryptor.encrypt(src.getPassword());
        logger.debug("Enscrypted pass {}", encryptedPassword);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(USERNAME, username);
        jsonObject.addProperty(PASSWORD, encryptedPassword);
        jsonObject.addProperty(ENCRYPTION_PASSWORD, encryptionPassword);

        return jsonObject;
    }
}
