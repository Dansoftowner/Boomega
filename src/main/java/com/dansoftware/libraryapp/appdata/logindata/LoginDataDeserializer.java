package com.dansoftware.libraryapp.appdata.logindata;

import com.dansoftware.libraryapp.db.Credentials;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginDataDeserializer implements JsonDeserializer<LoginData> {

    private static final String AUTO_LOGIN_CREDENTIALS = "credentials";
    private static final String LAST_DATABASES = "lastDatabases";
    private static final String SELECTED_DATABASE_INDEX = "selectedAccountIndex";
    private static final String AUTO_LOGIN_DATABASE_INDEX = "loggedAccountIndex";

    @Override
    public LoginData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //utility
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Credentials.class, new CredentialsDeserializer())
                .create();

        JsonObject loginDataJson = json.getAsJsonObject();

        Iterable<JsonElement> lastDatabasesIterable = loginDataJson.get(LAST_DATABASES).getAsJsonArray();
        List<DatabaseMeta> lastDatabases =
                StreamSupport.stream(lastDatabasesIterable.spliterator(), false) //converting the Iterable to a Stream
                        .map(JsonElement::getAsJsonObject)
                        .map(jsonObject -> gson.fromJson(jsonObject, DatabaseMeta.class))
                        .collect(Collectors.toList());

        int selectedDatabaseIndex = loginDataJson.get(SELECTED_DATABASE_INDEX).getAsInt();
        int autoDatabaseIndex = loginDataJson.get(AUTO_LOGIN_DATABASE_INDEX).getAsInt();

        LoginData loginData = new LoginData(lastDatabases);
        loginData.setAutoLoginDatabaseUnchecked(autoDatabaseIndex >= 0 ? lastDatabases.get(autoDatabaseIndex) : null);
        loginData.setAutoLoginCredentials(gson.fromJson(loginDataJson.get(AUTO_LOGIN_CREDENTIALS), Credentials.class));
        loginData.setSelectedDatabaseUnchecked(selectedDatabaseIndex >= 0 ? lastDatabases.get(selectedDatabaseIndex) : null);

        return loginData;
    }
}
