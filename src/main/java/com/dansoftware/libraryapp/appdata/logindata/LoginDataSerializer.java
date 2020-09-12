package com.dansoftware.libraryapp.appdata.logindata;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

public class LoginDataSerializer implements JsonSerializer<LoginData> {

    private static final Logger logger = LoggerFactory.getLogger(LoginDataSerializer.class);

    private static final String AUTO_LOGIN_CREDENTIALS = "credentials";
    private static final String LAST_DATABASES = "lastDatabases";
    private static final String SELECTED_DATABASE_INDEX = "selectedAccountIndex";
    private static final String AUTO_LOGIN_DATABASE_INDEX = "loggedAccountIndex";

    @Override
    public JsonElement serialize(LoginData src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credentials.class, new CredentialsSerializer())
                .create();

        List<DatabaseMeta> lastDatabases = src.getSavedDatabases();

        int autoLoginDatabaseIndex = lastDatabases.indexOf(src.getAutoLoginDatabase());
        int selectedDatabaseIndex = lastDatabases.indexOf(src.getSelectedDatabase());

        JsonObject jsonObjectResult = new JsonObject();
        jsonObjectResult.add(LAST_DATABASES, gson.toJsonTree(lastDatabases));
        jsonObjectResult.add(AUTO_LOGIN_CREDENTIALS, gson.toJsonTree(src.getAutoLoginCredentials()));
        jsonObjectResult.addProperty(SELECTED_DATABASE_INDEX, selectedDatabaseIndex);
        jsonObjectResult.addProperty(AUTO_LOGIN_DATABASE_INDEX, autoLoginDatabaseIndex);

        return jsonObjectResult;
    }
}
