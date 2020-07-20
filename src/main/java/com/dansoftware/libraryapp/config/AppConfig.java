package com.dansoftware.libraryapp.config;

import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.plugin.PluginClassLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Objects;
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

    public <T> T get(@NotNull Key<T> key) {
        JsonElement jsonElement = this.jsonObject.get(key.jsonKey);
        if (Objects.isNull(jsonElement)) {
            return key.defaultValue.get();
        }

        T value = key.constructingProcess.construct(jsonElement);
        return Objects.isNull(value) ? key.defaultValue.get() : value;
    }

    @SuppressWarnings("unused")
    public String getString(@NotNull String key) {
        return this.jsonObject.get(key).getAsString();
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(@NotNull String key) {
        return this.jsonObject.get(key).getAsBoolean();
    }

    @SuppressWarnings("unused")
    public int getInteger(@NotNull String key) {
        return this.jsonObject.get(key).getAsInt();
    }

    @SuppressWarnings("unused")
    public double getDouble(@NotNull String key) {
        return this.jsonObject.get(key).getAsDouble();
    }

    public <T> void set(Key<T> key, T value) {
        this.jsonObject.remove(key.jsonKey);

        JsonElement element = null;
        if (value != null)
            element = key.exportingProcess.export(value);

        this.jsonObject.add(key.jsonKey, element);
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public static class Key<T> {
        private final String jsonKey;
        private final Class<T> type;
        private final Supplier<T> defaultValue;
        private final ValueConstructingProcess<T> constructingProcess;
        private final ValueExportingProcess<T> exportingProcess;

        public Key(@NotNull String jsonKey,
                   @NotNull Class<T> type,
                   @NotNull Supplier<T> defaultValue) {
            this(jsonKey, type, defaultValue, ValueConstructingProcess.defaultProcess(type), ValueExportingProcess.defaultProcess());
        }

        public Key(@NotNull String jsonKey,
                   @NotNull Class<T> type,
                   @NotNull Supplier<T> defaultValue,
                   @NotNull ValueConstructingProcess<T> constructingProcess,
                   @NotNull ValueExportingProcess<T> valueExportingProcess) {
            this.jsonKey = jsonKey;
            this.type = type;
            this.defaultValue = defaultValue;
            this.constructingProcess = constructingProcess;
            this.exportingProcess = valueExportingProcess;
        }

        public static final Key<Locale> LOCALE = new Key<>("locale", Locale.class, Locale::getDefault);
        public static final Key<LoginData> LOGIN_DATA = new Key<>("loginData", LoginData.class, LoginData::new);
        public static final Key<Boolean> SEARCH_UPDATES = new Key<>("searchUpdates", boolean.class, () -> Boolean.TRUE);
        public static final Key<Theme> THEME = new Key<>("theme", Theme.class, Theme::getDefault, element -> {
            try {
                Class<?> themeClass = PluginClassLoader.getInstance().loadClass(element.getAsString());
                Constructor<?> constructor = themeClass.getConstructor();
                constructor.setAccessible(true);
                return (Theme) constructor.newInstance();
            } catch (ReflectiveOperationException | ClassCastException e) {
                LOGGER.error("Couldn't resolve theme ", e);
            }

            return null;
        }, value -> new JsonPrimitive(value.getClass().getName()));

    }
}
