package com.dansoftware.boomega.config.logindata;

import com.dansoftware.boomega.db.Credentials;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class LoginDataSerializer implements JsonSerializer<LoginData> {

    private static final Logger logger = LoggerFactory.getLogger(LoginDataSerializer.class);

    private static final String SAVED_DATABASES = "svdbs";
    private static final String SELECTED_DATABASE_INDEX = "slctdb";
    private static final String AUTO_LOGIN = "autolgn";
    private static final String AUTO_LOGIN_CREDENTIALS = "crdntls";

    @Override
    public JsonElement serialize(LoginData src, Type typeOfSrc, JsonSerializationContext context) {
        return buildJsonObject(src);
    }

    private JsonObject buildJsonObject(LoginData src) {
        Gson gson = buildGson();
        var json = new JsonObject();
        json.add(SAVED_DATABASES, gson.toJsonTree(src.getSavedDatabases()));
        json.addProperty(AUTO_LOGIN, src.isAutoLogin());
        json.addProperty(SELECTED_DATABASE_INDEX, src.getSelectedDatabaseIndex());
        if (src.isAutoLogin()) json.add(AUTO_LOGIN_CREDENTIALS, gson.toJsonTree(src.getAutoLoginCredentials()));
        return json;
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Credentials.class, new CredentialsSerializer()).create();
    }
}
