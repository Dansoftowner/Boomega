package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private final JsonObject jsonObject;

    public AppConfig() {
        this(new JsonObject());
    }

    public AppConfig(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public <T> T get(String key, Class<T> type) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (Objects.isNull(jsonElement)) {
            return null;
        }

        return new Gson().fromJson(jsonElement.toString(), type);
    }

    public <T> T get(Key<T> key) {
        if (Objects.nonNull(key.customRetriever)) {
            return key.customRetriever.apply(this.jsonObject);
        }

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
        private Function<JsonObject, T> customRetriever;

        public Key(@NotNull String property,
                   @NotNull Class<T> type,
                   @NotNull Supplier<T> defaultValue) {
            this.property = property;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public Key(@NotNull String property,
                   @NotNull Class<T> type,
                   @NotNull Supplier<T> defaultValue,
                   @Nullable Function<JsonObject, T> customRetriever) {
            this.property = property;
            this.type = type;
            this.defaultValue = defaultValue;
            this.customRetriever = customRetriever;
        }

        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class, Locale::getDefault);
        public static final Key<LoginData> LOGIN_DATA = new Key<>("loginData", LoginData.class, LoginData::new);
        public static final Key<Boolean> SEARCH_UPDATES = new Key<>("searchUpdates", boolean.class, () -> Boolean.TRUE);
        public static final Key<Theme> THEME = new Key<>("theme", Theme.class, Theme::getDefault, jsonObject -> {
            JsonElement themeJsonElement = jsonObject.get("theme");
            if (Objects.nonNull(themeJsonElement)) {
                try {
                    Class<?> themeClass = PluginClassLoader.getInstance().loadClass(themeJsonElement.getAsString());
                    Constructor<?> constructor = themeClass.getConstructor();
                    constructor.setAccessible(true);
                    return (Theme) constructor.newInstance();
                } catch (ReflectiveOperationException | ClassCastException e) {
                    LOGGER.error("Couldn't resolve theme ", e);
                }
            }

            return null;
        });
    }
}
