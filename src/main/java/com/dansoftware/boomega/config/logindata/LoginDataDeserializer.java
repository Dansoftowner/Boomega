package com.dansoftware.boomega.config.logindata;

import com.dansoftware.boomega.db.Credentials;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginDataDeserializer implements JsonDeserializer<LoginData> {

    private static final String SAVED_DATABASES = "svdbs";
    private static final String SELECTED_DATABASE_INDEX = "slctdb";
    private static final String AUTO_LOGIN = "autolgn";
    private static final String AUTO_LOGIN_CREDENTIALS = "crdntls";

    private final Gson gson;

    public LoginDataDeserializer() {
        this.gson = buildGson();
    }

    @Override
    public LoginData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return asLoginData(json.getAsJsonObject());
    }

    private LoginData asLoginData(JsonObject json) {
        final List<DatabaseMeta> savedDatabases = getSavedDatabases(json);
        final Credentials autoLoginCredentials = gson.fromJson(json.get(AUTO_LOGIN_CREDENTIALS), Credentials.class);
        final int selectedDatabaseIndex = json.get(SELECTED_DATABASE_INDEX).getAsInt();
        final boolean autoLogin = json.get(AUTO_LOGIN).getAsBoolean();
        return buildLoginData(savedDatabases, autoLoginCredentials, selectedDatabaseIndex, autoLogin);
    }

    private LoginData buildLoginData(List<DatabaseMeta> savedDatabases,
                                     Credentials autoLoginCredentials,
                                     int selectedDatabaseIndex,
                                     boolean autoLogin) {
        final LoginData loginData = new LoginData(savedDatabases);
        loginData.setAutoLoginCredentials(autoLoginCredentials);
        loginData.setSelectedDatabaseIndex(selectedDatabaseIndex);
        loginData.setAutoLogin(autoLogin);
        return loginData;
    }

    private List<DatabaseMeta> getSavedDatabases(JsonObject json) {
        Iterable<JsonElement> lastDatabasesIterable = json.get(SAVED_DATABASES).getAsJsonArray();
        return StreamSupport.stream(lastDatabasesIterable.spliterator(), false) //converting the Iterable to a Stream
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> gson.fromJson(jsonObject, DatabaseMeta.class))
                .collect(Collectors.toList());
    }


    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Credentials.class, new CredentialsDeserializer())
                .create();
    }
}
