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

import com.dansoftware.boomega.database.api.DatabaseField;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.google.gson.*;
import javafx.collections.FXCollections;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
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
        final boolean autoLogin = json.get(AUTO_LOGIN).getAsBoolean();
        final int selectedDatabaseIndex = json.get(SELECTED_DATABASE_INDEX).getAsInt();
        final Map<DatabaseField, String> autoLoginCredentials = if (autoLogin) gson.fromJson(json.get(AUTO_LOGIN_CREDENTIALS), Map.class) else null;
        return buildLoginData(savedDatabases, autoLoginCredentials, selectedDatabaseIndex, autoLogin);
    }

    private LoginData buildLoginData(List<DatabaseMeta> savedDatabases,
                                     Map<DatabaseField, String> autoLoginCredentials,
                                     int selectedDatabaseIndex,
                                     boolean autoLogin) {
        final LoginData loginData = new LoginData(FXCollections.observableArrayList(savedDatabases), savedDatabases.get(selectedDatabaseIndex), autoLoginCredentials);
        loginData.setAutoLoginCredentials(aautoLoginCredentials);
        loginData.setSelectedDatabaseIndex(selectedDatabaseIndex);
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
