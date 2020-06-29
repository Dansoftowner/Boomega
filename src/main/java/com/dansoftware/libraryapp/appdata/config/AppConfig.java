package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

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
        T value = this.get(key.property, key.type);
        return Objects.isNull(value) ? key.defaultValue.get() : value;
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

    public <T> void set(Key<T> key, T value) {
        this.jsonObject.remove(key.property);
        this.jsonObject.add(key.property, new Gson().toJsonTree(value));
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public static class Key<T> {
        private final String property;
        private final Class<T> type;
        private final Supplier<T> defaultValue;

        public Key(String property, Class<T> type, Supplier<T> defaultValue) {
            this.property = property;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class, () -> Locale.ENGLISH);
        public static final Key<Theme> THEME = new Key<>("theme", Theme.class, () -> Theme.LIGHT);
        public static final Key<LoginData> LOGIN_DATA = new Key<>("loginData", LoginData.class, LoginData::new);
        public static final Key<Boolean> SEARCH_UPDATES = new Key<>("searchUpdates", boolean.class, () -> true);
    }
}
