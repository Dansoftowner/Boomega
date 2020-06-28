package com.dansoftware.libraryapp.appdata.config.new_;

import com.dansoftware.libraryapp.db.Account;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

public class AppConfig {

    private final JsonObject jsonObject;

    public AppConfig() {
        this(new JsonObject());
    }

    public AppConfig(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public <T> T get(String key, Class<T> type) {
        return new Gson().fromJson(this.jsonObject.get(key).toString(), type);
    }

    public <T> T get(Key<T> key) {
        return this.get(key.value, key.type);
    }

    public String getString(String key) {
        return this.jsonObject.get(key).getAsString();
    }

    public boolean getBoolean(String key) {
        return this.jsonObject.get(key).getAsBoolean();
    }

    public int getInteger(String key) {
        return this.jsonObject.get(key).getAsInt();
    }

    public double getDouble(String key) {
        return this.jsonObject.get(key).getAsDouble();
    }



    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public static class Key<T> {
        private final String value;
        private final Class<T> type;

        private Key(String value, Class<T> type) { this.value = value; this.type = type; }

        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class);
        public static final Key<Theme> THEME = new Key<>("theme", Theme.class);
       /* LOGIN DATA ---------------
        public static final Key<List> LAST_DATABASES = new Key<>("lastFiles", List.class);
        public static final Key<Account> LOGGED_ACCOUNT = new Key<>("loggedAccount", Account.)
*/
    }
}
